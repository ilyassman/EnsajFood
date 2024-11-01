<?php
include_once RACINE . '/connexion/Connexion.php';
class LoginService {
    private $conn;

    function __construct() {
        $this->conn = new Connexion();
    }
    public function login(){
         // Récupérer les données envoyées par l'application
   
    $email = $_POST["email"];
    $password =$_POST["password"];
    $stmt = $this->conn->getConnexion()->prepare("SELECT * FROM utilisateur WHERE email = :email");
    $stmt->bindParam(':email', $email);
    $stmt->execute();

    $user = $stmt->fetch(PDO::FETCH_ASSOC);
  
   
    if ($user && $password==$user['mot_de_passe']) {
        echo json_encode(["success" => true, "message" => "Connexion réussie","id"=>$user['id'],"nom"=>$user['nom']." ".$user["prenom"],"email"=>$user["email"],"role"=>$user["role"]]);
    } else {
        echo json_encode(["success" => false, "message" => "Email ou mot de passe incorrect"]);
    }
} 
public function changePassword() {
    $data = json_decode(file_get_contents("php://input"), true);

    // Vérifier si les données nécessaires sont présentes
    if (!isset($data["email"]) || !isset($data["new_password"])) {
        echo json_encode([
            "success" => false,
            "message" => "Données manquantes"
        ]);
        return;
    }
    $email = $data["email"];
    $newPassword = $data["new_password"];

    // Vérifier si l'utilisateur existe
    $stmt = $this->conn->getConnexion()->prepare("SELECT * FROM utilisateur WHERE email = :email");
    $stmt->bindParam(':email', $email);
    $stmt->execute();

    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($user ) {
        // Mettre à jour le mot de passe
        $updateStmt = $this->conn->getConnexion()->prepare("UPDATE utilisateur SET mot_de_passe = :new_password WHERE email = :email");
        $updateStmt->bindParam(':new_password', $newPassword);
        $updateStmt->bindParam(':email', $email);

        if ($updateStmt->execute()) {
            echo json_encode([
                "success" => true,
                "message" => "Mot de passe modifié avec succès"
            ]);
        } else {
            echo json_encode([
                "success" => false,
                "message" => "Erreur lors de la modification du mot de passe"
            ]);
        }
    } else {
        echo json_encode([
            "success" => false,
            "message" => "L'ancien mot de passe est incorrect"
        ]);
    }
}

public function registerUser() {
    $email = $_POST["email"];
    $nom = $_POST["nom"];
    $prenom = $_POST["prenom"];
    $mot_de_passe = $_POST["password"];
    $stmt = $this->conn->getConnexion()->prepare("SELECT * FROM utilisateur WHERE email = :email");
    $stmt->bindParam(':email', $email);
    $stmt->execute();
    
    if ($stmt->rowCount() > 0) {
        echo json_encode([
            "success" => false, 
            "message" => "L'email est déjà utilisé"
        ]);
    } else {
        // Insérer le nouvel utilisateur
        $insertStmt = $this->conn->getConnexion()->prepare("INSERT INTO utilisateur (email, nom, prenom, mot_de_passe,role) VALUES (:email, :nom, :prenom, :mot_de_passe,1)");
        $insertStmt->bindParam(':email', $email);
        $insertStmt->bindParam(':nom', $nom);
        $insertStmt->bindParam(':prenom', $prenom);
        $insertStmt->bindParam(':mot_de_passe', $mot_de_passe);
        
        if ($insertStmt->execute()) {
            echo json_encode([
                "success" => true, 
                "message" => "Inscription réussie"
            ]);
        } else {
            echo json_encode([
                "success" => false, 
                "message" => "Erreur lors de l'inscription"
            ]);
        }
    }
}
public function updateUser() {
    $id = $_POST["id"];
    $nom = $_POST["nom"];
    $prenom = $_POST["prenom"];
    $email = $_POST["email"];
    $mot_de_passe = $_POST["password"];

    // Vérifier si l'email est déjà utilisé par un autre utilisateur
    $stmt = $this->conn->getConnexion()->prepare("SELECT * FROM utilisateur WHERE email = :email AND id != :id");
    $stmt->bindParam(':email', $email);
    $stmt->bindParam(':id', $id);
    $stmt->execute();

    if ($stmt->rowCount() > 0) {
        echo json_encode([
            "success" => false,
            "message" => "L'email est déjà utilisé par un autre utilisateur"
        ]);
    } else {
        // Récupérer le mot de passe actuel de l'utilisateur s'il est vide
        if (empty($mot_de_passe)) {
            $stmt = $this->conn->getConnexion()->prepare("SELECT mot_de_passe FROM utilisateur WHERE id = :id");
            $stmt->bindParam(':id', $id);
            $stmt->execute();
            $user = $stmt->fetch(PDO::FETCH_ASSOC);
            $mot_de_passe = $user['mot_de_passe'];
        }

        // Mettre à jour les informations de l'utilisateur
        $updateStmt = $this->conn->getConnexion()->prepare("UPDATE utilisateur SET email = :email, nom = :nom, prenom = :prenom, mot_de_passe = :mot_de_passe WHERE id = :id");
        $updateStmt->bindParam(':email', $email);
        $updateStmt->bindParam(':nom', $nom);
        $updateStmt->bindParam(':prenom', $prenom);
        $updateStmt->bindParam(':mot_de_passe', $mot_de_passe);
        $updateStmt->bindParam(':id', $id);

        if ($updateStmt->execute()) {
            echo json_encode([
                "success" => true,
                "message" => "Mise à jour réussie"
            ]);
        } else {
            echo json_encode([
                "success" => false,
                "message" => "Erreur lors de la mise à jour"
            ]);
        }
    }
}

    }





?>