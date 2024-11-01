<?php
include_once RACINE . '/classes/Category.php';
include_once RACINE . '/connexion/Connexion.php';
include_once RACINE . '/dao/ICategDao.php';

class CategoryService implements ICategDao {
    private $connexion;

    function __construct() {
        $this->connexion = new Connexion();
    }

    // Créer une nouvelle catégorie
    public function create($o) {
        $image = $o->getImagePath();
        $nom_image = time() . '.jpg';  // Générer un nom unique pour l'image

        // Correction du chemin d'upload
        $chemin_upload = __DIR__ . '/../uploads/' . $nom_image;

        // Assurez-vous que le dossier uploads existe
        if (!file_exists(__DIR__ . '/../uploads/')) {
            mkdir(__DIR__ . '/../uploads/', 0777, true);
        }

        // Décoder l'image base64
        $donnees_image = base64_decode(preg_replace('#^data:image/\w+;base64,#i', '', $image));

        // Sauvegarder le fichier image
        if (file_put_contents($chemin_upload, $donnees_image)) {
            // L'image a été sauvegardée avec succès
            $nom_image_bdd = 'uploads/' . $nom_image; // Chemin relatif pour la BDD
        } else {
            // Erreur lors de la sauvegarde de l'image
            $nom_image_bdd = '';
        }

        $query = "INSERT INTO Category (`id`, `name`, `imagePath`) "
               . "VALUES (NULL, '" . $o->getName() . "', '" . $nom_image_bdd . "');";

        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute() or die('Erreur SQL');
    }

    // Supprimer une catégorie
    public function delete($o) {
        $query = "DELETE FROM Category WHERE id = " . $o->getId();
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute() or die('Erreur SQL');
    }

    // Trouver toutes les catégories
    public function findAll() {
        $categories = array();
        $query = "SELECT * FROM Category";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        while ($c = $req->fetch(PDO::FETCH_OBJ)) {
            $categories[] = new Category($c->id, $c->imagePath, $c->name);
        }
        return $categories;
    }

    // Trouver une catégorie par ID
    public function findById($id) {
        $query = "SELECT * FROM Category WHERE id = " . $id;
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        if ($c = $req->fetch(PDO::FETCH_OBJ)) {
            $category = new Category($c->id, $c->imagePath, $c->name);
        }
        return $category;
    }

    // Mettre à jour une catégorie
    public function update($o) {
        $updateImageQuery = "";

        // Gestion de l'image si elle est fournie
        if ($o->getImagePath() && !empty($o->getImagePath())) {
            $nom_image = time() . '.jpg';  // Générer un nom unique pour l'image
            $chemin_upload = __DIR__ . '/../uploads/' . $nom_image;

            // Assurez-vous que le dossier uploads existe
            if (!file_exists(__DIR__ . '/../uploads/')) {
                mkdir(__DIR__ . '/../uploads/', 0777, true);
            }

            // Décoder l'image base64
            $donnees_image = base64_decode(preg_replace('#^data:image/\w+;base64,#i', '', $o->getImagePath()));

            // Sauvegarder le fichier image
            if (file_put_contents($chemin_upload, $donnees_image)) {
                // L'image a été sauvegardée avec succès
                $nom_image_bdd = 'uploads/' . $nom_image;
                $updateImageQuery = ", `imagePath` = '" . $nom_image_bdd . "'";

                // Suppression de l'ancienne image si elle existe
                $stmt = $this->connexion->getConnexion()->prepare("SELECT imagePath FROM Category WHERE id = ?");
                $stmt->execute([$o->getId()]);
                $oldImage = $stmt->fetchColumn();

                if ($oldImage && file_exists(__DIR__ . '/../' . $oldImage)) {
                    unlink(__DIR__ . '/../' . $oldImage);
                }
            }
        }

        $query = "UPDATE `Category` SET 
                  `name` = '" . $o->getName() . "'" . 
                  $updateImageQuery . 
                  " WHERE `id` = " . $o->getId();

        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute() or die('Erreur SQL');
    }

    // Retourner toutes les catégories pour une API
    public function findAllApi() {
        $query = "SELECT * FROM Category";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        return $req->fetchAll(PDO::FETCH_ASSOC);
    }
}
?>
