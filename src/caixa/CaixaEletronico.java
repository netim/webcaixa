package caixa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@SessionScoped
@ManagedBean(name = "caixaEletronico")
public class CaixaEletronico {
	private Boolean selecao = false;
	private Map<String, String> opcoes;
	private String opcaoSelecionada;
	private List<Caixa> cedulas;
	private Integer quantia;
	private Session session;
	private SessionFactory sessionFactory;
	private int[] indiceCedulas;
	private int[] indiceQtd;
	private int[] indiceValor;
	private int[][] matrizCombinacoes;
	private ArrayList<Integer[]> linhasValor;
	private ArrayList<Integer> linhasIndice;

	public String getOpcaoSelecionada() {
		return "";
	}

	public void setOpcaoSelecionada(String opcaoSelecionada) {
		this.opcaoSelecionada = opcaoSelecionada;
	}

	public Map<String, String> getOpcoes() {
		return opcoes;
	}

	public void setOpcoes(Map<String, String> opcoes) {
		this.opcoes = opcoes;
	}

	public Boolean getSelecao() {
		return selecao;
	}

	public void setSelecao(Boolean selecao) {
		this.selecao = selecao;
	}

	public void confirmar() {
		if (opcaoSelecionada == "") {
			Random rand = new Random();
			opcaoSelecionada = String.valueOf(rand.nextInt(linhasIndice.size()));
		}
		setSelecao(false);
		sessionFactory = new Configuration().configure().buildSessionFactory();
		session = sessionFactory.openSession();
		session.beginTransaction();
		cedulas = session.createQuery("from Caixa").list();
		for (Caixa c : cedulas) {
			for (int i = 0; i < 3; i++) {
				if (c.getValor() == (int) indiceCedulas[linhasValor.get(Integer.parseInt(opcaoSelecionada))[i]]) {
					c.setQtd(c.getQtd() - indiceQtd[linhasValor.get(Integer.parseInt(opcaoSelecionada))[i]]);
					if (c.getQtd() == 0)
						reabastecer(c);
					session.update(c);
				}
			}
		}
		session.getTransaction().commit();
		session.close();

		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

	public void reabastecer(Caixa c) {
		switch (c.getValor()) {
		case 10:
			c.setQtd(100);
			break;
		case 20:
			c.setQtd(40);
			break;
		case 50:
			c.setQtd(50);
			break;
		default:
			c.setQtd(10);
		}

	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		sessionFactory = new Configuration().configure().buildSessionFactory();
		session = sessionFactory.openSession();
		session.beginTransaction();
		cedulas = session.createQuery("from Caixa").list();
		session.getTransaction().commit();
		session.close();

		if (sessionFactory != null) {
			sessionFactory.close();
		}

	}

	public List<Caixa> getCedulas() {
		return cedulas;
	}

	public String getQuantia() {
		return "";
	}

	public void setQuantia(String quantia) {
		this.quantia = Integer.parseInt(quantia);

	}

	public void sacar() {
		if (selecao == false) {
			setSelecao(true);
			int saque = this.quantia;
			sessionFactory = new Configuration().configure().buildSessionFactory();
			session = sessionFactory.openSession();
			session.beginTransaction();
			Caixa cedula10 = session.get(Caixa.class, 1);
			Caixa cedula20 = session.get(Caixa.class, 2);
			Caixa cedula50 = session.get(Caixa.class, 3);
			Caixa cedula100 = session.get(Caixa.class, 4);
			int qtd10 = saque / 10 > cedula10.getQtd() ? cedula10.getQtd() : saque / 10;
			int qtd20 = saque / 20 > cedula20.getQtd() ? cedula20.getQtd() : saque / 20;
			int qtd50 = saque / 50 > cedula50.getQtd() ? cedula50.getQtd() : saque / 50;
			int qtd100 = saque / 100 > cedula100.getQtd() ? cedula100.getQtd() : saque / 100;
			int totalCombinacoes = qtd10 + qtd20 + qtd50 + qtd100 + 4;
			indiceCedulas = new int[totalCombinacoes];
			indiceQtd = new int[totalCombinacoes];
			indiceValor = new int[totalCombinacoes];
			int linhas = totalCombinacoes * (totalCombinacoes - 1) * (totalCombinacoes - 2) / 6;
			matrizCombinacoes = new int[linhas][3];
			for (int i = 0; i < totalCombinacoes; i++) {
				if (i >= 0 && i <= qtd10) {
					indiceCedulas[i] = 10;
					indiceQtd[i] = i;
				}
				if (i > qtd10 && i <= (qtd10 + qtd20 + 1)) {
					indiceCedulas[i] = 20;
					indiceQtd[i] = i - qtd10 - 1;
				}
				if (i > (qtd10 + qtd20 + 1) && i <= (qtd10 + qtd20 + qtd50 + 2)) {
					indiceCedulas[i] = 50;
					indiceQtd[i] = i - qtd10 - qtd20 - 2;
				}
				if (i > (qtd10 + qtd20 + qtd50 + 2) && i <= (qtd10 + qtd20 + qtd50 + qtd100 + 3)) {
					indiceCedulas[i] = 100;
					indiceQtd[i] = i - qtd10 - qtd20 - qtd50 - 3;
				}
				indiceValor[i] = indiceCedulas[i] * indiceQtd[i];
			}
			int linha = 0;
			linhasValor = new ArrayList<>();
			linhasIndice = new ArrayList<>();
			opcoes = new HashMap<>();
			int contador = 0;
			for (int i = 0; i < totalCombinacoes - 2; i++) {
				for (int j = i + 1; j < totalCombinacoes - 1; j++) {
					for (int k = j + 1; k < totalCombinacoes; k++) {
						matrizCombinacoes[linha][0] = i;
						matrizCombinacoes[linha][1] = j;
						matrizCombinacoes[linha][2] = k;
						if (indiceValor[i] + indiceValor[j] + indiceValor[k] == saque) {

							String opcao = "";
							if (indiceCedulas[i] == indiceCedulas[j] || indiceCedulas[i] == indiceCedulas[k]
									|| indiceCedulas[j] == indiceCedulas[k]) {
								if (indiceCedulas[i] == indiceCedulas[j] && indiceCedulas[j] == indiceCedulas[k]) {
									opcao += String.valueOf(indiceQtd[i] + indiceQtd[j] + indiceQtd[k]) + "x"
											+ String.valueOf(indiceCedulas[i]);
								} else {
									if (indiceCedulas[i] == indiceCedulas[j]) {
										opcao += String.valueOf(indiceQtd[i] + indiceQtd[j]) + "x"
												+ String.valueOf(indiceCedulas[i]);
										if (indiceValor[k] != 0) {
											opcao += " + " + String.valueOf(indiceQtd[k]) + "x"
													+ String.valueOf(indiceCedulas[k]);
										}
									} else {
										if (indiceCedulas[j] == indiceCedulas[k]) {
											if (indiceValor[i] != 0) {
												opcao += String.valueOf(indiceQtd[i]) + "x"
														+ String.valueOf(indiceCedulas[i]) + " + ";
											}
											opcao += String.valueOf(indiceQtd[j] + indiceQtd[k]) + "x"
													+ String.valueOf(indiceCedulas[j]);
										} else {
											if (indiceCedulas[i] == indiceCedulas[k]) {
												opcao += String.valueOf(indiceQtd[i] + indiceQtd[k]) + "x"
														+ String.valueOf(indiceCedulas[i]);
												if (indiceValor[j] != 0) {
													opcao += " + " + String.valueOf(indiceQtd[i]) + "x"
															+ String.valueOf(indiceCedulas[i]);
												}
											}
										}
									}
								}
							} else {
								if (indiceValor[i] != 0)
									opcao += String.valueOf(indiceQtd[i]) + "x" + String.valueOf(indiceCedulas[i]);
								if (indiceValor[j] != 0) {
									if (indiceValor[i] != 0)
										opcao += " + ";
									opcao += String.valueOf(indiceQtd[j]) + "x" + String.valueOf(indiceCedulas[j]);
								}
								if (indiceValor[k] != 0) {
									if (indiceValor[i] != 0 || indiceValor[j] != 0)
										opcao += " + ";
									opcao += String.valueOf(indiceQtd[k]) + "x" + String.valueOf(indiceCedulas[k]);
								}
							}

							if (!opcoes.containsKey(opcao)) {
								opcoes.put(opcao, String.valueOf(contador));
								linhasValor.add(new Integer[] { i, j, k });
								linhasIndice.add(contador);
								contador++;
							}

						}

						linha++;
					}
				}
			}
			session.getTransaction().commit();
			session.close();

			if (sessionFactory != null) {
				sessionFactory.close();
			}
		}
	}

}