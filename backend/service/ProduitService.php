<?php
include_once RACINE . '/classes/Produit.php';
include_once RACINE . '/connexion/Connexion.php';
include_once RACINE . '/dao/IProduitDao.php';

class ProduitService implements IProduitDao {
    private $connexion;

    function __construct() {
        $this->connexion = new Connexion();
    }

    // Créer un nouveau produit avec gestion d'image
    public function addProduit() {
        
        $nom = $_POST['nom'];
$description = $_POST['description'];
$prix = $_POST['prix'];
$categorie_id = $_POST['categorie_id'];
$image = $_POST['image'];

// Créer l'objet Produit
$produit = new Produit($nom,$description,$prix,$categorie_id,$image);
        $image = $produit->getImage();
        $nom_image = time() . '.jpg';  // Générer un nom unique pour l'image

        // Chemin d'upload
        $chemin_upload = __DIR__ . '/../uploads/produit/' . $nom_image;

        // Assurez-vous que le dossier uploads existe
        if (!file_exists(__DIR__ . '/../uploads/produit/')) {
            mkdir(__DIR__ . '/../uploads/produit/', 0777, true);
        }

        // Décoder l'image base64
        $donnees_image = base64_decode(preg_replace('#^data:image/\w+;base64,#i', '', $image));

        // Sauvegarder le fichier image
        if (file_put_contents($chemin_upload, $donnees_image)) {
            $nom_image_bdd =$nom_image; // Chemin relatif pour la BDD
        } else {
            $nom_image_bdd = '';
        }

        $query = "INSERT INTO produit (nom, description, prix, categorie_id, image_url) 
                  VALUES (:nom, :description, :prix, :categorie, :image)";
        $stmt = $this->connexion->getConnexion()->prepare($query);
        $stmt->bindValue(':nom', $produit->getNom());
        $stmt->bindValue(':description', $produit->getDescription());
        $stmt->bindValue(':prix', $produit->getPrix());
        $stmt->bindValue(':categorie', $produit->getCategorie());
        $stmt->bindValue(':image', $nom_image_bdd);
        $stmt->execute();
    }

    // Supprimer un produit
    public function supprimerProduit($id) {
        $query = "DELETE FROM produit WHERE id = :id";
        $stmt = $this->connexion->getConnexion()->prepare($query);
        $stmt->bindValue(':id', $id);
        $stmt->execute();
    }

    // Trouver tous les produits
    public function obtenirTousLesProduits($id) {
        $query = "select * from produit WHERE categorie_id=:id";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->bindValue(':id',$id);
        $req->execute();
        return $req->fetchAll(PDO::FETCH_ASSOC);
    }
    public function findAll() {
        $query = "select * from produit";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        return $req->fetchAll(PDO::FETCH_ASSOC);
    }

    // Trouver un produit par ID
    public function obtenirProduitParId($id) {
        $query = "SELECT * FROM produit WHERE id = :id";
        $stmt = $this->connexion->getConnexion()->prepare($query);
        $stmt->bindValue(':id', $id);
        $stmt->execute();
      
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        
    }

    // Mettre à jour un produit avec gestion de l'image
    public function updateProduit($id) {
        $nom = $_POST['nom'];
        $description = $_POST['description'];
        $prix = $_POST['prix'];
        
        $image = isset($_POST['image']) ? $_POST['image'] : null;
    
        // Préparation du nom de l'image si une nouvelle image est fournie
        $nom_image_bdd = null;
        if ($image) {
            // Nom unique pour la nouvelle image
            $nom_image = time() . '.jpg';
            $chemin_upload = __DIR__ . '/../uploads/produit/' . $nom_image;
    
            // Vérifier et créer le dossier uploads s'il n'existe pas
            if (!file_exists(__DIR__ . '/../uploads/produit/')) {
                mkdir(__DIR__ . '/../uploads/produit/', 0777, true);
            }
    
            // Décoder l'image base64
            $donnees_image = base64_decode(preg_replace('#^data:image/\w+;base64,#i', '', $image));
    
            // Sauvegarder la nouvelle image et mettre à jour le chemin
            if (file_put_contents($chemin_upload, $donnees_image)) {
                $nom_image_bdd = $nom_image; // Nom de l'image pour la BDD
            }
        }
    
        // Requête SQL pour mettre à jour le produit
        $query = "UPDATE produit SET nom = :nom, description = :description, prix = :prix";
        if ($nom_image_bdd) {
            $query .= ", image_url = :image"; // Ajout de l'image seulement si elle est fournie
        }
        $query .= " WHERE id = :id";
    
        $stmt = $this->connexion->getConnexion()->prepare($query);
        $stmt->bindValue(':nom', $nom);
        $stmt->bindValue(':description', $description);
        $stmt->bindValue(':prix', $prix);
       
        if ($nom_image_bdd) {
            $stmt->bindValue(':image', $nom_image_bdd);
        }
        $stmt->bindValue(':id', $id);
        $stmt->execute();
    
        // Réponse de succès
        header('Content-Type: application/json');
        $response = [
            "status" => "success",
            "message" => "Produit mis à jour avec succès"
        ];
        echo json_encode($response);
    }
    

    // Retourner tous les produits pour une API
    public function findAllApi() {
        $query = "SELECT * FROM produits";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        return $req->fetchAll(PDO::FETCH_ASSOC);
    }
}
?>
