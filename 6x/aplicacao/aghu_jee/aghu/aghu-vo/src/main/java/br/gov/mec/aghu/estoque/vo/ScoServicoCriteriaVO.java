package br.gov.mec.aghu.estoque.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoSiasgServico;

/**
 * Classe VO responsável pelos critérios de busca por parâmetros de regras
 * orçamentárias.
 * 
 * @author mlcruz
 */
public class ScoServicoCriteriaVO implements java.io.Serializable {
	private static final long serialVersionUID = 3450359287001272532L;

	private Integer codigo;
	private String nome;
	private DominioSituacao situacao;
	private ScoGrupoServico grupo;
	private FsoGrupoNaturezaDespesa grupoNatureza;
	private FsoNaturezaDespesa natureza;
	private ScoSiasgServico catSer;
	private DominioSimNao contrato;
	private Date dataCadastro;
	private String descricao;
	private String observacao;
	private Date dtDigitacao;
	private Date dtAlteracao;
	private Date dtDesativacao;
	private RapServidores servidor;
	private RapServidores servidorDesativado;

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public ScoGrupoServico getGrupo() {
		return grupo;
	}

	public void setGrupo(ScoGrupoServico grupo) {
		this.grupo = grupo;
	}

	public FsoGrupoNaturezaDespesa getGrupoNatureza() {
		return grupoNatureza;
	}

	public void setGrupoNatureza(FsoGrupoNaturezaDespesa grupoNatureza) {
		this.grupoNatureza = grupoNatureza;
	}

	public FsoNaturezaDespesa getNatureza() {
		return natureza;
	}

	public void setNatureza(FsoNaturezaDespesa natureza) {
		this.natureza = natureza;
	}

	public DominioSimNao getContrato() {
		return contrato;
	}

	public void setContrato(DominioSimNao contrato) {
		this.contrato = contrato;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RapServidores getServidorDesativado() {
		return servidorDesativado;
	}

	public void setServidorDesativado(RapServidores servidorDesativado) {
		this.servidorDesativado = servidorDesativado;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public ScoSiasgServico getCatSer() {
		return catSer;
	}

	public void setCatSer(ScoSiasgServico catSer) {
		this.catSer = catSer;
	}

	public Date getDtAlteracao() {
		return dtAlteracao;
	}

	public void setDtAlteracao(Date dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}

	public Date getDtDesativacao() {
		return dtDesativacao;
	}

	public void setDtDesativacao(Date dtDesativacao) {
		this.dtDesativacao = dtDesativacao;
	}

	public Date getDtDigitacao() {
		return dtDigitacao;
	}

	public void setDtDigitacao(Date dtDigitacao) {
		this.dtDigitacao = dtDigitacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}