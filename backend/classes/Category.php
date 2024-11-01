<?php
class Category {
    private $id;
    private $imagePath;
    private $name;

    public function __construct($id = 0, $imagePath = "", $name = "") {
        $this->id = $id;
        $this->imagePath = $imagePath;
        $this->name = $name;
    }

    // Getters
    public function getId() {
        return $this->id;
    }

    public function getImagePath() {
        return $this->imagePath;
    }

    public function getName() {
        return $this->name;
    }

    // Setters
    public function setId($id) {
        $this->id = $id;
    }

    public function setImagePath($imagePath) {
        $this->imagePath = $imagePath;
    }

    public function setName($name) {
        $this->name = $name;
    }

    
}
?>
