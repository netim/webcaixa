package caixa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="caixa")
public class Caixa {
	
    private int valor;	
    private int qtd; 
    private int id;
    
    public Caixa() {
    	
    }
    
    public Caixa(int valor, int qtd) {
    	this.valor = valor;
    	this.qtd = qtd;
    	
    }
    
    @Column(name="valor")
    public int getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}

	@Column(name="qtd")
	public int getQtd() {
		return qtd;
	}

	public void setQtd(int qtd) {
		this.qtd = qtd;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
