<?php
if ($_SERVER["REQUEST_METHOD"] == "POST") {
 include_once '../racine.php';
 include_once RACINE . '/service/CommandeService.php';
 loadAll();
}
function loadAll() {
 $es = new CommandeService();
 $id=$_GET['id'];
 header('Content-type: application/json');
 echo json_encode($es->findCommandeByIdUser($id));
}
