<?php
require '../vendor/autoload.php';
require '../connexion/Connexion.php'; // Assurez-vous d'inclure votre classe de connexion

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

header("Content-Type: application/json");

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405); 
    echo json_encode(["error" => "Méthode non autorisée. Utilisez POST."]);
    exit;
}

$data = json_decode(file_get_contents("php://input"));

if (!isset($data->to) || !isset($data->verification_code)) {
    http_response_code(400); 
    echo json_encode(["error" => "Les champs 'to' et 'verification_code' sont obligatoires."]);
    exit;
}

// Vérification de l'email dans la base de données
$conn = new Connexion();
$email = $data->to;

$stmt = $conn->getConnexion()->prepare("SELECT COUNT(*) FROM utilisateur WHERE email = :email");
$stmt->bindParam(':email', $email);
$stmt->execute();
$count = $stmt->fetchColumn();

if ($count == 0) {
    http_response_code(404);
    echo json_encode(["error" => "L'email spécifié n'existe pas."]);
    exit;
}

// Récupérer le code de vérification depuis la requête JSON
$verificationCode = $data->verification_code;

// Configuration de PHPMailer
$mail = new PHPMailer(true);

try {
    // Configuration du serveur SMTP pour Gmail
    $mail->isSMTP();
    $mail->Host       = 'smtp.gmail.com';
    $mail->SMTPAuth   = true;
    $mail->Username   = 'ilyassmandour2002@gmail.com'; // Remplacez par votre adresse Gmail
    $mail->Password   = 'icmv ebsj reqb tela'; // Mot de passe d'application
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port       = 587;

    // Configuration de l'email
    $mail->setFrom('ilyassmandour2002@gmail.com', 'ENSAJFOOD');
    $mail->addAddress($email); 
    $mail->Subject = "Code de vérification pour la réinitialisation de votre mot de passe";
    
    // Corps de l'email en HTML
    $mail->isHTML(true);
    $mail->Body = "
        <div style='font-family: Arial, sans-serif; color: #333;'>
            <h2 style='color: #4CAF50;'>Réinitialisation de votre mot de passe</h2>
            <p>Bonjour,</p>
            <p>Vous avez demandé à réinitialiser votre mot de passe. Utilisez le code ci-dessous pour procéder à la réinitialisation :</p>
            <div style='font-size: 24px; font-weight: bold; color: #333; margin: 20px 0;'>
                $verificationCode
            </div>
            <p>Cordialement,<br>ENSAJFOOD</p>
        </div>
    ";

    if ($mail->send()) {
        echo json_encode(["message" => "Email envoyé avec succès."]);
    } else {
        echo json_encode(["error" => "Erreur lors de l'envoi de l'email."]);
    }
} catch (Exception $e) {
    echo json_encode(["error" => "Exception: " . $mail->ErrorInfo]);
}
?>
