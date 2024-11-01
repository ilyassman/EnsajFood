<?php
if ($_SERVER["REQUEST_METHOD"] == "POST") {
 include_once '../racine.php';
 include_once RACINE . '/service/LoginService.php';
 loadAll();
}
function loadAll() {
 $es = new LoginService();
 header('Content-type: application/json');
 echo json_encode($es->registerUser());
}
