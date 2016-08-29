package br.gov.mec.aghu.casca.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;

/**
 * VO utilizado como filtro em pesquisa.
 * 
 * @author lcmoura
 * 
 */
public class FiltroPerfilJnVO implements Serializable {
	private static final long serialVersionUID = -6814394186514289836L;

	private Integer idPerfil;
	private String nome;
	private DominioOperacoesJournal operacao;
	private Date dataInicio;
	private Date dataFim;
	private String alteradoPor;

	public FiltroPerfilJnVO(Integer idPerfil, String nome,
			DominioOperacoesJournal operacao, Date dataInicio, Date dataFim,
			String alteradoPor) {
		super();
		this.idPerfil = idPerfil;
		this.nome = nome;
		this.operacao = operacao;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.alteradoPor = alteradoPor;
	}

	public Integer getIdPerfil() {
		return idPerfil;
	}

	public void setIdPerfil(Integer idPerfil) {
		this.idPerfil = idPerfil;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}

	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public String getAlteradoPor() {
		return alteradoPor;
	}

	public void setAlteradoPor(String alteradoPor) {
		this.alteradoPor = alteradoPor;
	}
}
