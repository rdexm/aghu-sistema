package br.gov.mec.aghu.exames.solicitacao.vo;

import java.util.List;

import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelPatologista;

public class AmostraVO {
	private boolean selecionado;
	private String exame;
	private String material;
	private Long numeroExame;
	private String patologista;
	private boolean agrupada;
	private AelItemSolicitacaoExames itemSolicitacaoExame;
	private AelConfigExLaudoUnico tipoExame;
	private MaterialVO materialAmostra;
	private List<AelPatologista> patologistasResponsaveis;
	private List<AmostraVO> amostrasAgrupadas;
	private ExameAndamentoVO exameAndamentoVO;
	private Integer numeroAmostra;
	
	public boolean isSelecionado() {
		return selecionado;
	}
	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
	public String getExame() {
		return exame;
	}
	public void setExame(String exame) {
		this.exame = exame;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public String getMaterial() {
		return material;
	}
	public Long getNumeroExame() {
		return numeroExame;
	}
	public void setNumeroExame(Long numeroExame) {
		this.numeroExame = numeroExame;
	}
	public String getPatologista() {
		return patologista;
	}
	public void setPatologista(String patologista) {
		this.patologista = patologista;
	}
	public AelConfigExLaudoUnico getTipoExame() {
		return tipoExame;
	}
	public void setTipoExame(AelConfigExLaudoUnico tipoExame) {
		this.tipoExame = tipoExame;
	}
	public MaterialVO getMaterialAmostra() {
		return materialAmostra;
	}
	public void setMaterialAmostra(MaterialVO materialAmostra) {
		this.materialAmostra = materialAmostra;
	}
	public List<AelPatologista> getPatologistasResponsaveis() {
		return patologistasResponsaveis;
	}
	public void setPatologistasResponsaveis(List<AelPatologista> patologistasResponsaveis) {
		this.patologistasResponsaveis = patologistasResponsaveis;
	}
	public List<AmostraVO> getAmostrasAgrupadas() {
		return amostrasAgrupadas;
	}
	public void setAmostrasAgrupadas(List<AmostraVO> amostrasAgrupadas) {
		this.amostrasAgrupadas = amostrasAgrupadas;
	}
	public AelItemSolicitacaoExames getItemSolicitacaoExame() {
		return itemSolicitacaoExame;
	}
	public void setItemSolicitacaoExame(AelItemSolicitacaoExames itemSolicitacaoExame) {
		this.itemSolicitacaoExame = itemSolicitacaoExame;
	}
	public boolean isAgrupada() {
		return agrupada;
	}
	public void setAgrupada(boolean agrupada) {
		this.agrupada = agrupada;
	}

	public void setExameAndamentoVO(ExameAndamentoVO exameAndamentoVO) {
		this.exameAndamentoVO = exameAndamentoVO;
	}

	public ExameAndamentoVO getExameAndamentoVO() {
		return exameAndamentoVO;
	}

	public void setNumeroAmostra(Integer numeroAmostra) {
		this.numeroAmostra = numeroAmostra;
	}

	public Integer getNumeroAmostra() {
		return numeroAmostra;
	}
}