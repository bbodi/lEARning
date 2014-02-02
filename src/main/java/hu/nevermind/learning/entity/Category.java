package hu.nevermind.learning.entity;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@NamedQueries({
	@NamedQuery(name = "Category.findAll", query = "SELECT c FROM Category c"),
})
public class Category extends BaseEntity {
	@OneToMany(mappedBy = "category")
	private List<Subcategory> subcategorys;
	
	private String name;

	public Category() {
	}
	
	public Category(String name) {
		this.name = name;
	}

	public List<Subcategory> getSubcategorys() {
		return subcategorys;
	}

	public void setSubcategorys(List<Subcategory> subcategorys) {
		this.subcategorys = subcategorys;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
