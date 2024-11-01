<?php
class Produit {
    private $id;
    private $nom;
    private $description;
    private $prix;
    private $categorie;
    private $image;

    public function __construct($nom, $description, $prix, $categorie, $image, $id = null) {
        $this->nom = $nom;
        $this->description = $description;
        $this->prix = $prix;
        $this->categorie = $categorie;
        $this->image = $image;
        $this->id = $id;
    }

    public function getId() {
        return $this->id;
    }

    public function getNom() {
        return $this->nom;
    }

    public function getDescription() {
        return $this->description;
    }

    public function getPrix() {
        return $this->prix;
    }

    public function getCategorie() {
        return $this->categorie;
    }

    public function getImage() {
        return $this->image;
    }
}
?>
