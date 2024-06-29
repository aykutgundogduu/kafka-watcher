
var editBootstrapConfigModal = document.getElementById('editBootstrapConfig')
console.log(editBootstrapConfigModal)
if (editBootstrapConfigModal) {
    editBootstrapConfigModal.addEventListener('show.bs.modal', function (event) {
        // Button that triggered the modal
        var button = event.relatedTarget
        // Extract info from data-bs-* attributes
        var serverIp = button.getAttribute('data-bs-server-ip')
        var serverPort = button.getAttribute('data-bs-server-port')
        var cluster = button.getAttribute('data-bs-cluster')
        var configName = button.getAttribute('data-bs-config-name')
        var configValue = button.getAttribute('data-bs-config-value')
        // If necessary, you could initiate an AJAX request here
        // and then do the updating in a callback.
        //
        // Update the modal's content.

        var clusterInput = editBootstrapConfigModal.querySelector('[aria-label="Cluster"]')
        var serverInput = editBootstrapConfigModal.querySelector('[aria-label="Server"]')
        var configKeyInput = editBootstrapConfigModal.querySelector('[aria-label="Config Key"]')
        var configValueInput = editBootstrapConfigModal.querySelector('[aria-label="Config Value"]')
        clusterInput.value= cluster

        serverInput.value = serverIp + ":" + serverPort
        configKeyInput.value = configName
        configValueInput.value = configValue
    })
}

var addNewTopicModal = document.getElementById('addMewTopicModal')
if (addNewTopicModal) {
    addNewTopicModal.addEventListener('show.bs.modal', function (event) {

    })
}

