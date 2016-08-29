package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.List;



/**
 * VO utilizado pela estoria 5945
 * 
 * @author Guilherme Finotti Carvalho
 *
 */
public class RelatorioExamesPacienteVO implements Serializable {

	private static final long serialVersionUID = 8655362043307700454L;
	private List<RelatorioExamesPacienteExamesVO> exames;
	private List<RelatorioExamesPacienteDetalhesVO> examesDetalhes;
	private List<String> tipoExame;
	private String nome; // 26 - NOME de Q_SUE
	private String ltoLtoId; // 27 - lto_lto_id de Q_SUE
	private String identificacao; // 28 + 29 + 30
	
	//CAMAPOS AUXILIARES
	private String prontuario;
	private Boolean recemNascido;
	private String dthrFim;
	private String indImprime; 
	private String calNomeSumario;	
	
	/**
	 * 
	 */
	public RelatorioExamesPacienteVO() {
	}

	public List<RelatorioExamesPacienteExamesVO> getExames() {
		return exames;
	}

	public void setExames(List<RelatorioExamesPacienteExamesVO> exames) {
		this.exames = exames;
	}

	public List<RelatorioExamesPacienteDetalhesVO> getExamesDetalhes() {
		return examesDetalhes;
	}
	
	public List<String> getTipoExame() {
		return tipoExame;
	}

	public void setTipoExame(List<String> tipoExame) {
		this.tipoExame = tipoExame;
	}

	public void setExamesDetalhes(List<RelatorioExamesPacienteDetalhesVO> examesDetalhes) {
		this.examesDetalhes = examesDetalhes;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	public String getIdentificacao() {
		return identificacao;
	}

	public void setIdentificacao(String identificacao) {
		this.identificacao = identificacao;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public Boolean getRecemNascido() {
		return recemNascido;
	}

	public void setRecemNascido(Boolean recemNascido) {
		this.recemNascido = recemNascido;
	}

	public String getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(String dthrFim) {
		this.dthrFim = dthrFim;
	}

	public String getIndImprime() {
		return indImprime;
	}

	public void setIndImprime(String indImprime) {
		this.indImprime = indImprime;
	}

	public String getCalNomeSumario() {
		return calNomeSumario;
	}

	public void setCalNomeSumario(String calNomeSumario) {
		this.calNomeSumario = calNomeSumario;
	}
	

}
