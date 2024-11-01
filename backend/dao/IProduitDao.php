<?php

interface IProduitDao {
    public function addProduit();
    public function obtenirProduitParId($id);
    public function obtenirTousLesProduits($id);
    public function updateProduit(Produit $produit);
    public function supprimerProduit($id);
}
?>
