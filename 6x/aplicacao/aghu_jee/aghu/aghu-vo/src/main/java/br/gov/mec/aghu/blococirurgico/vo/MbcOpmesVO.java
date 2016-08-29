package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

import br.gov.mec.aghu.dominio.DominioRequeridoItemRequisicao;
import br.gov.mec.aghu.dominio.DominioTabelaGrupoExcludente;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcMateriaisItemOpmes;
import br.gov.mec.aghu.model.ScoMaterial;

public class MbcOpmesVO implements Serializable {

	private static final long serialVersionUID = 4418729048192642723L;
	
	private String itemProcedimentoHospitalar;
	private String solicitacaoMaterial;
	private Short qtdeAut;
	private Integer qtdeSol;
	private BigDecimal valorUnit;
	
	private Integer qtdeSolicitadaMaterial;
	private Integer qtdeSolicitadaMaterialOld;
	private boolean selecionado;
	private ScoMaterial material;
	
	private boolean excluir;
	
	private MbcItensRequisicaoOpmes itensRequisicaoOpmes;
	private MbcMateriaisItemOpmes mbcMateriaisItemOpmes;
	private MbcOpmesVO voQuebra;
	private List<MbcOpmesVO> filhos;
	private DominioTabelaGrupoExcludente cor;
	
	public String corQtdeSol;
	
	private Short itemSeq;
	private Integer codigoMaterial;
	private Long codTabela;
	private String descricaoMarcasComerciais;
	private String unidadeMaterial;
	private Integer codigoMarcasComerciais;
	private Double valorUnitario;
	
	public enum Fields {

		DDT_SEQ("ddtSeq");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public MbcOpmesVO() {
	}
	
	public MbcOpmesVO(MbcItensRequisicaoOpmes item, boolean excluir, Long iphCompCod, String iphCompDescr, MbcMateriaisItemOpmes mbcMateriaisItemOpmes) {
		this.setItensRequisicaoOpmes(item);
		this.setFilhos(new ArrayList<MbcOpmesVO>());

		this.setExcluir(excluir);

		this.setQtdeAut(BigDecimal.ZERO.shortValue());
		this.setQtdeSol(0);
		this.setValorUnit(BigDecimal.ZERO);
		this.setSelecionado(true);
		this.setQtdeSolicitadaMaterial(0);

		ScoMaterial material = null;
		String codigoDescricao = "";

		if (mbcMateriaisItemOpmes != null) {
			material = mbcMateriaisItemOpmes.getMaterial();
			this.setMbcMateriaisItemOpmes(mbcMateriaisItemOpmes);
		}
		if (DominioRequeridoItemRequisicao.REQ.equals(item.getRequerido())
				|| DominioRequeridoItemRequisicao.NRQ.equals(item.getRequerido())) {
			codigoDescricao = iphCompCod + " - " + iphCompDescr;

		} else if (DominioRequeridoItemRequisicao.ADC.equals(item.getRequerido())
				|| DominioRequeridoItemRequisicao.NOV.equals(item.getRequerido())) {
			if (material == null) {
				this.setSelecionado(false);
				this.setQtdeSolicitadaMaterial(0);
			}
			if (DominioRequeridoItemRequisicao.ADC.equals(item.getRequerido())) {
				codigoDescricao = "(Material Adicionado pelo Usuário)";
			} else {
				codigoDescricao = "(Nova Solicitação de Material)";
				this.setSolicitacaoMaterial(item.getSolicitacaoNovoMaterial());
			}
			
		}
		if (mbcMateriaisItemOpmes != null) {
			this.setQtdeSolicitadaMaterial(mbcMateriaisItemOpmes.getQuantidadeSolicitada() == null ? 0 : mbcMateriaisItemOpmes.getQuantidadeSolicitada());
		} else {
			this.setQtdeSolicitadaMaterial(item.getQuantidadeSolicitada().intValue());
		}
		this.setQtdeSol(item.getQuantidadeSolicitada());

		if (item.getValorUnitarioIph() != null) {
			this.setValorUnit(item.getValorUnitarioIph());
		}

		this.setQtdeAut(item.getQuantidadeAutorizadaSus());
		this.setItemProcedimentoHospitalar(codigoDescricao);
		this.setMaterial(material);
	}

	public String getItemProcedimentoHospitalar() {
		return itemProcedimentoHospitalar;
	}

	public void setItemProcedimentoHospitalar(String itemProcedimentoHospitalar) {
		this.itemProcedimentoHospitalar = itemProcedimentoHospitalar;
	}

	public Short getQtdeAut() {
		return qtdeAut;
	}

	public void setQtdeAut(Short qtdeAut) {
		this.qtdeAut = qtdeAut;
	}

	public Integer getQtdeSol() {
		return qtdeSol;
	}

	public void setQtdeSol(Integer qtdeSol) {
		this.qtdeSol = qtdeSol;
	}

	public BigDecimal getValorUnit() {
		return valorUnit;
	}

	public void setValorUnit(BigDecimal valorUnit) {
		this.valorUnit = valorUnit;
	}

	public BigDecimal getValorTotal() {
		return valorUnit.multiply(new BigDecimal(qtdeSol));
	}

	public boolean isExcluir() {
		return excluir;
	}

	public void setExcluir(boolean excluir) {
		this.excluir = excluir;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	public Integer getQtdeSolicitadaMaterial() {
		return qtdeSolicitadaMaterial;
	}
	public void setQtdeSolicitadaMaterial(Integer qtdeSolicitadaMaterial) {
		qtdeSolicitadaMaterialOld = this.qtdeSolicitadaMaterial;
		this.qtdeSolicitadaMaterial = qtdeSolicitadaMaterial;
	}
	public MbcItensRequisicaoOpmes getItensRequisicaoOpmes() {
		return itensRequisicaoOpmes;
	}
	public void setItensRequisicaoOpmes(MbcItensRequisicaoOpmes itensRequisicaoOpmes) {
		this.itensRequisicaoOpmes = itensRequisicaoOpmes;
	}
	public MbcOpmesVO getVoQuebra() {
		return voQuebra;
	}
	public void setVoQuebra(MbcOpmesVO voQuebra) {
		this.voQuebra = voQuebra;
	}


	public List<MbcOpmesVO> getFilhos() {
		return filhos;
	}


	public void setFilhos(List<MbcOpmesVO> filhos) {
		this.filhos = filhos;
	}
	
	public String getCodigoEDescricao() {
		return material == null ? solicitacaoMaterial : material.getCodigoENome();
	}


	public MbcMateriaisItemOpmes getMbcMateriaisItemOpmes() {
		return mbcMateriaisItemOpmes;
	}


	public void setMbcMateriaisItemOpmes(MbcMateriaisItemOpmes mbcMateriaisItemOpmes) {
		this.mbcMateriaisItemOpmes = mbcMateriaisItemOpmes;
	}

	public DominioTabelaGrupoExcludente getCor() {
		return cor;
	}

	public void setCor(DominioTabelaGrupoExcludente cor) {
		this.cor = cor;
	}

	public String getCorQtdeSol() {
		return corQtdeSol;
	}

	public void setCorQtdeSol(String corQtdeSol) {
		this.corQtdeSol = corQtdeSol;
	}

	public Integer getQtdeSolicitadaMaterialOld() {
		return qtdeSolicitadaMaterialOld;
	}

	public void setQtdeSolicitadaMaterialOld(Integer qtdeSolicitadaMaterialOld) {
		this.qtdeSolicitadaMaterialOld = qtdeSolicitadaMaterialOld;
	}

	public String getSolicitacaoMaterial() {
		return solicitacaoMaterial;
	}

	public void setSolicitacaoMaterial(String solicitacaoMaterial) {
		this.solicitacaoMaterial = solicitacaoMaterial;
	}
	
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setItemSeq(Short itemSeq) {
		this.itemSeq = itemSeq;
	}

	public Short getItemSeq() {
		return itemSeq;
	}

	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}

	public Long getCodTabela() {
		return codTabela;
	}

	public String getDescricaoMarcasComerciais() {
		return descricaoMarcasComerciais;
	}

	public void setDescricaoMarcasComerciais(String descricaoMarcasComerciais) {
		this.descricaoMarcasComerciais = descricaoMarcasComerciais;
	}

	public String getUnidadeMaterial() {
		return unidadeMaterial;
	}

	public void setUnidadeMaterial(String unidadeMaterial) {
		this.unidadeMaterial = unidadeMaterial;
	}

	public Integer getCodigoMarcasComerciais() {
		return codigoMarcasComerciais;
	}

	public void setCodigoMarcasComerciais(Integer codigoMarcasComerciais) {
		this.codigoMarcasComerciais = codigoMarcasComerciais;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codTabela == null) ? 0 : codTabela.hashCode());
		result = prime * result
				+ ((codigoMaterial == null) ? 0 : codigoMaterial.hashCode());
		result = prime
				* result
				+ ((itemProcedimentoHospitalar == null) ? 0
						: itemProcedimentoHospitalar.hashCode());
		result = prime * result + ((itemSeq == null) ? 0 : itemSeq.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		MbcOpmesVO other = (MbcOpmesVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSolicitacaoMaterial(), other.getSolicitacaoMaterial());
		umEqualsBuilder.append(this.getMaterial(), other.getMaterial());
		umEqualsBuilder.append(this.getQtdeSol(), other.getQtdeSol());
		umEqualsBuilder.append(this.getDescricaoMarcasComerciais(), other.getDescricaoMarcasComerciais());
		
		return umEqualsBuilder.isEquals();
	}
	
	
	
}
