/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.nevermind.learning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 *
 * @author sharp
 */
@Entity
public class Note extends BaseEntity {
	
	@Column(length = 10000)
	private String note;

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	
}
