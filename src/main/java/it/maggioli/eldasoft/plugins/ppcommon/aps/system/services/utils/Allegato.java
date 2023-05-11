/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils;

/**
 *
 * @author marco.perazzetta
 */
public class Allegato {

	private String nome;
	private byte[] contenuto;

	public Allegato(String nome, byte[] contenuto) {
		this.nome = nome;
		this.contenuto = contenuto;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the contenuto
	 */
	public byte[] getContenuto() {
		return contenuto;
	}

	/**
	 * @param contenuto the contenuto to set
	 */
	public void setContenuto(byte[] contenuto) {
		this.contenuto = contenuto;
	}

}
