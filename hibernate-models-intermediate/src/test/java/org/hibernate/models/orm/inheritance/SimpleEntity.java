package org.hibernate.models.orm.inheritance;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Basic;

/**
 * @author Steve Ebersole
 */
@Entity
public class SimpleEntity {
	@Id
	private Integer id;
	@Basic
	private String name;
	private Integer someInteger;

	protected SimpleEntity() {
		// for Hibernate use
	}

	public SimpleEntity(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
