package com.hospital.app
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) { Root() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Root(vm: VM = viewModel()) {
    var tab by remember { mutableIntStateOf(0) }
    var dialog by remember { mutableStateOf(false) }
    val patients by vm.patients.collectAsState()
    val doctors by vm.doctors.collectAsState()
    val appts by vm.appts.collectAsState()
    val titles = listOf(R.string.home, R.string.patients, R.string.doctors, R.string.appts)
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
        bottomBar = { NavigationBar { titles.forEachIndexed { i, t ->
            NavigationBarItem(selected = tab == i, onClick = { tab = i }, icon = { Text(if (tab == i) "●" else "○") }, label = { Text(stringResource(t)) }) } } },
        floatingActionButton = { if (tab > 0) FloatingActionButton(onClick = { dialog = true }) { Text("+") } }
    ) { pad ->
        Box(Modifier.padding(pad).padding(12.dp)) {
            when (tab) {
                0 -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Stat(stringResource(R.string.patients), patients.size)
                    Stat(stringResource(R.string.doctors), doctors.size)
                    Stat(stringResource(R.string.appts), appts.size)
                }
                1 -> LazyColumn { items(patients, key = { it.id }) { Row2(it.name, it.phone) { vm.del(it) } } }
                2 -> LazyColumn { items(doctors, key = { it.id }) {
                    Row2(it.name, "${it.specialty} • ${it.city} • ${it.fee}\n" + stringResource(if (it.verified) R.string.verified else R.string.unverified)) { vm.del(it) } } }
                else -> LazyColumn { items(appts, key = { it.id }) { Row2(it.date, "${it.patientName} → ${it.doctorName}") { vm.del(it) } } }
            }
        }
    }
    if (dialog) {
        when (tab) {
            1 -> PatientForm({ dialog = false }) { vm.add(it); dialog = false }
            2 -> DoctorForm({ dialog = false }) { vm.add(it); dialog = false }
            3 -> ApptForm(patients.map { it.name }, doctors.map { it.name }, { dialog = false }) { vm.add(it); dialog = false }
        }
    }
}

@Composable fun Stat(label: String, n: Int) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.titleMedium); Text("$n", style = MaterialTheme.typography.headlineSmall) } }
}

@Composable fun Row2(title: String, sub: String, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text(title, style = MaterialTheme.typography.titleMedium); Text(sub) }
            TextButton(onClick = onDelete) { Text("✕") } } }
}

@Composable fun Field(label: String, v: String, onChange: (String) -> Unit, err: Boolean) {
    OutlinedTextField(v, onChange, label = { Text(label) }, isError = err, singleLine = true,
        supportingText = { if (err) Text(stringResource(R.string.required)) }, modifier = Modifier.fillMaxWidth())
}

@Composable fun FormDialog(onDismiss: () -> Unit, onSave: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onSave) { Text(stringResource(R.string.save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
        text = { Column(verticalArrangement = Arrangement.spacedBy(4.dp), content = content) })
}

@Composable fun PatientForm(onDismiss: () -> Unit, onSave: (Patient) -> Unit) {
    var n by remember { mutableStateOf("") }; var p by remember { mutableStateOf("") }; var chk by remember { mutableStateOf(false) }
    FormDialog(onDismiss, { chk = true; if (n.isNotBlank() && p.isNotBlank()) onSave(Patient(name = n.trim(), phone = p.trim())) }) {
        Field(stringResource(R.string.name), n, { n = it }, chk && n.isBlank()); Field(stringResource(R.string.phone), p, { p = it }, chk && p.isBlank()) }
}

@Composable fun DoctorForm(onDismiss: () -> Unit, onSave: (Doctor) -> Unit) {
    var n by remember { mutableStateOf("") }; var s by remember { mutableStateOf("") }; var c by remember { mutableStateOf("") }
    var f by remember { mutableStateOf("") }; var v by remember { mutableStateOf(false) }; var chk by remember { mutableStateOf(false) }
    FormDialog(onDismiss, { chk = true; if (n.isNotBlank() && s.isNotBlank()) onSave(Doctor(name = n.trim(), specialty = s.trim(), city = c.trim(), fee = f.trim(), verified = v)) }) {
        Field(stringResource(R.string.name), n, { n = it }, chk && n.isBlank()); Field(stringResource(R.string.specialty), s, { s = it }, chk && s.isBlank())
        Field(stringResource(R.string.city), c, { c = it }, false); Field(stringResource(R.string.fee), f, { f = it }, false)
        Row(verticalAlignment = Alignment.CenterVertically) { Switch(v, { v = it }); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.verified)) } }
}

@Composable fun ApptForm(pn: List<String>, dn: List<String>, onDismiss: () -> Unit, onSave: (Appointment) -> Unit) {
    var p by remember { mutableStateOf("") }; var d by remember { mutableStateOf("") }; var dt by remember { mutableStateOf("") }; var chk by remember { mutableStateOf(false) }
    val ok = Regex("\\d{4}-\\d{2}-\\d{2}").matches(dt)
    FormDialog(onDismiss, { chk = true; if (p.isNotBlank() && d.isNotBlank() && ok) onSave(Appointment(patientName = p, doctorName = d, date = dt)) }) {
        Picker(stringResource(R.string.patients), pn, p) { p = it }; Picker(stringResource(R.string.doctors), dn, d) { d = it }
        Field(stringResource(R.string.date), dt, { dt = it }, chk && !ok) }
}

@Composable fun Picker(label: String, opts: List<String>, sel: String, onSel: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box { OutlinedButton(onClick = { open = true }, Modifier.fillMaxWidth()) { Text(if (sel.isBlank()) label else sel) }
        DropdownMenu(open, { open = false }) { opts.forEach { o -> DropdownMenuItem(text = { Text(o) }, onClick = { onSel(o); open = false }) } } }
}
