<?php
include_once RACINE . '/connexion/Connexion.php';


class CommandeService {
    private $conn;

    function __construct() {
        $this->conn = new Connexion();
    }

    // Créer une nouvelle catégorie
    public function create() {
        $requestBody = file_get_contents('php://input');
        $donnees = json_decode($requestBody, true);
        $stmt = $this->conn->getConnexion()->prepare("INSERT INTO commande (utilisateur_id, montant_total) VALUES (:utilisateur_id, :montant_total)");
        $stmt->bindParam(':utilisateur_id',  $donnees['iduser']);
        $stmt->bindParam(':montant_total',  $donnees['total']);
        $stmt->execute();
        $commande_id = $this->conn->getConnexion()->lastInsertId();
        $produits = $donnees['produits'];
        foreach ($produits as $produit) {
            $stmt = $this->conn->getConnexion()->prepare("INSERT INTO detail_commande (commande_id, produit_id, quantite, prix_unitaire) VALUES (:commande_id, :produit_id, :quantite, :prix_unitaire)");
            $stmt->bindParam(':commande_id', $commande_id);
            $stmt->bindParam(':produit_id', $produit['produit_id']);
            $stmt->bindParam(':quantite', $produit['quantite']);
            $stmt->bindParam(':prix_unitaire', $produit['prix_unitaire']);
            $stmt->execute();
        }
        header('Content-Type: application/json');
        $response = [
            "status" => "success",
            "message" => "Commande bien ajoutée",
            "commande_id" => $produits[1] // Si tu veux retourner l'ID de la commande
        ];
        echo json_encode($response);
    }
    public function getAllCommande() {
        $commandes = [];
        $query = "SELECT commande.id AS idcommande, commande.montant_total AS montant, commande.statut,
                         produit.nom AS produit_nom, detail_commande.quantite AS quantite
                  FROM commande
                  JOIN detail_commande ON commande.id = detail_commande.commande_id
                  JOIN produit ON produit.id = detail_commande.produit_id
                  ";
                  
        $req = $this->conn->getConnexion()->prepare($query);
        
        $req->execute();
        $result = $req->fetchAll(PDO::FETCH_ASSOC);
    
        // Regroupement des produits par commande
        foreach ($result as $row) {
            $commandeId = $row['idcommande'];
            
            // Si la commande n'existe pas encore dans le tableau, on l'initialise
            if (!isset($commandes[$commandeId])) {
                $commandes[$commandeId] = [
                    'idcommande' => $commandeId,
                    'montant' => $row['montant'],
                    'status' => $row['statut'],
                    'produits' => []
                ];
            }
    
            // Ajouter le produit dans la liste des produits de la commande
            $commandes[$commandeId]['produits'][] = [
                'nom' => $row['produit_nom'],
                'quantite' => $row['quantite']
            ];
        }
    
        // Réinitialiser les clés pour obtenir un tableau indexé
        return array_values($commandes);
    }
    public function findCommandeByIdUser($id) {
        $commandes = [];
        $query = "SELECT commande.id AS idcommande, commande.montant_total AS montant, commande.statut,
                         produit.nom AS produit_nom, detail_commande.quantite AS quantite
                  FROM commande
                  JOIN detail_commande ON commande.id = detail_commande.commande_id
                  JOIN produit ON produit.id = detail_commande.produit_id
                  WHERE commande.utilisateur_id = :id";
                  
        $req = $this->conn->getConnexion()->prepare($query);
        $req->bindValue(':id', $id);
        $req->execute();
        $result = $req->fetchAll(PDO::FETCH_ASSOC);
    
        // Regroupement des produits par commande
        foreach ($result as $row) {
            $commandeId = $row['idcommande'];
            
            // Si la commande n'existe pas encore dans le tableau, on l'initialise
            if (!isset($commandes[$commandeId])) {
                $commandes[$commandeId] = [
                    'idcommande' => $commandeId,
                    'montant' => $row['montant'],
                    'status' => $row['statut'],
                    'produits' => []
                ];
            }
    
            // Ajouter le produit dans la liste des produits de la commande
            $commandes[$commandeId]['produits'][] = [
                'nom' => $row['produit_nom'],
                'quantite' => $row['quantite']
            ];
        }
    
        // Réinitialiser les clés pour obtenir un tableau indexé
        return array_values($commandes);
    }
    public function delete() {
        $commande_id=$_POST["id"];
        try {
            // Préparer et exécuter la requête pour supprimer la commande
            $stmt = $this->conn->getConnexion()->prepare("DELETE FROM commande WHERE id = :commande_id");
            $stmt->bindParam(':commande_id', $commande_id);
            $stmt->execute();
    
            // Réponse de succès
            header('Content-Type: application/json');
            $response = [
                "status" => "success",
                "message" => "Commande supprimée avec succès"
            ];
            echo json_encode($response);
        } catch (Exception $e) {
            // Réponse d'erreur
            header('Content-Type: application/json');
            $response = [
                "status" => "error",
                "message" => "Erreur lors de la suppression de la commande: " . $e->getMessage()
            ];
            echo json_encode($response);
        }
    }
    public function updateStatus($commande_id, $new_status) {
        try {
            // Préparer et exécuter la requête pour mettre à jour le statut de la commande
            $stmt = $this->conn->getConnexion()->prepare("UPDATE commande SET statut = :new_status WHERE id = :commande_id");
            $stmt->bindParam(':new_status', $new_status);
            $stmt->bindParam(':commande_id', $commande_id);
            $stmt->execute();
    
            // Réponse de succès
            header('Content-Type: application/json');
            $response = [
                "status" => "success",
                "message" => "Statut de la commande mis à jour avec succès"
            ];
            echo json_encode($response);
        } catch (Exception $e) {
            // Réponse d'erreur
            header('Content-Type: application/json');
            $response = [
                "status" => "error",
                "message" => "Erreur lors de la mise à jour du statut de la commande: " . $e->getMessage()
            ];
            echo json_encode($response);
        }
    }
    
    
    

}
?>
