package br.gov.mec.aghu.internacao.vo;

import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AinCaracteristicaLeito;
import br.gov.mec.aghu.model.AinLeitos;

public class AinLeitosVO implements BaseBean {

	private static final long serialVersionUID = -8583292686181199582L;

	private String leitoID;
	private String leito;
	private String descricaoUnidade;
	private String nomeEspecialidade;
	private Boolean indConsClinUnidade;
	private Boolean indConsEsp;
	private Boolean indBloqLeitoLimpeza;
	private Boolean indPertenceRefl;
	private DominioSituacao indSituacao;
	private Integer qtCaracteristicas;
	private boolean novoLeito;
	
	private AinLeitos ainLeito;
	private List<AinCaracteristicaLeito> ainCaracteristicas;
	private List<AinCaracteristicaLeito> caracteristicasInseridas;
	private List<AinCaracteristicaLeito> caracteristicasExcluidas;
	
	public AinLeitosVO() {
		super();
		caracteristicasInseridas = new ArrayList<AinCaracteristicaLeito>();
		caracteristicasExcluidas = new ArrayList<AinCaracteristicaLeito>();
	}
	
	public AinLeitosVO(String leitoID) {
		this();
		this.leitoID = leitoID;
	}

	public AinLeitosVO(AinLeitos ainLeito) {
		this();
		this.ainLeito = ainLeito;
	}



	public enum Fields {
		
		DESCRICAO_UNIDADE("descricaoUnidade"),
		NOME_ESPECIALIDADE("nomeEspecialidade"),
		IND_CONS_CLIN_UNIDADE("indConsClinUnidade"),
		IND_CONS_ESP("indConsEsp"),
		IND_BLOQ_LEITO_LIMPEZA("indBloqLeitoLimpeza"),
		IND_PERTENCE_REFL("indPertenceRefl"),
		SITUACAO("indSituacao"),
		QT_CARACTERISTICAS("qtCaracteristicas"),
		LEITO_ID("leitoID"), 
		LEITO("leito");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public String getDescricaoUnidade() {
		return descricaoUnidade;
	}

	public void setDescricaoUnidade(String descricaoUnidade) {
		this.descricaoUnidade = descricaoUnidade;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public Boolean getIndConsClinUnidade() {
		return indConsClinUnidade;
	}

	public void setIndConsClinUnidade(Boolean indConsClinUnidade) {
		this.indConsClinUnidade = indConsClinUnidade;
	}

	public Boolean getIndConsEsp() {
		return indConsEsp;
	}

	public void setIndConsEsp(Boolean indConsEsp) {
		this.indConsEsp = indConsEsp;
	}

	public Boolean getIndBloqLeitoLimpeza() {
		return indBloqLeitoLimpeza;
	}

	public void setIndBloqLeitoLimpeza(Boolean indBloqLeitoLimpeza) {
		this.indBloqLeitoLimpeza = indBloqLeitoLimpeza;
	}

	public Boolean getIndPertenceRefl() {
		return indPertenceRefl;
	}

	public void setIndPertenceRefl(Boolean indPertenceRefl) {
		this.indPertenceRefl = indPertenceRefl;
	}

	public Integer getQtCaracteristicas() {
		return qtCaracteristicas;
	}

	public void setQtCaracteristicas(Integer qtCaracteristicas) {
		this.qtCaracteristicas = qtCaracteristicas;
	}

	public String getLeitoID() {
		return leitoID;
	}

	public void setLeitoID(String leitoID) {
		this.leitoID = leitoID;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public AinLeitos getAinLeito() {
		return ainLeito;
	}

	public void setAinLeito(AinLeitos ainLeito) {
		this.ainLeito = ainLeito;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((leitoID == null) ? 0 : leitoID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AinLeitosVO) {
			AinLeitosVO leito = (AinLeitosVO) obj;
			if (this.getLeitoID() != null) {
				return leito.getLeitoID().equalsIgnoreCase(this.getLeitoID());
			} else {
				return leito.getLeito().equalsIgnoreCase(this.getLeito());
			}
		}
		return false;
	}

	public List<AinCaracteristicaLeito> getCaracteristicasInseridas() {
		return caracteristicasInseridas;
	}

	public void setCaracteristicasInseridas(
			List<AinCaracteristicaLeito> caracteristicasInseridas) {
		this.caracteristicasInseridas = caracteristicasInseridas;
	}

	public List<AinCaracteristicaLeito> getCaracteristicasExcluidas() {
		return caracteristicasExcluidas;
	}

	public void setCaracteristicasExcluidas(
			List<AinCaracteristicaLeito> caracteristicasExcluidas) {
		this.caracteristicasExcluidas = caracteristicasExcluidas;
	}

	public List<AinCaracteristicaLeito> getAinCaracteristicas() {
		return ainCaracteristicas;
	}

	public void setAinCaracteristicas(
			List<AinCaracteristicaLeito> ainCaracteristicas) {
		this.ainCaracteristicas = ainCaracteristicas;
	}

	public boolean isNovoLeito() {
		return novoLeito;
	}

	public void setNovoLeito(boolean novoLeito) {
		this.novoLeito = novoLeito;
	}
	
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

}
