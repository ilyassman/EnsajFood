<?php
interface ICategDao {
    public function create(Category $category);
    public function findById($id);
    public function findAll();
    public function update(Category $category);
    public function delete($id);
}
?>
