<?php
if ($_SERVER["REQUEST_METHOD"] == "POST") {
 include_once '../racine.php';
 include_once RACINE . '/service/ProduitService.php';
 loadAll();
}
function loadAll() {
 $es = new ProduitService();
 $id=$_GET["id"];
 header('Content-type: application/json');
 echo json_encode($es->obtenirTousLesProduits($id));
}
