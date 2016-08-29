package br.gov.mec.aghu.internacao.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * Classe responsável por agrupar informações a serem exibidos no grid da
 * pesquisa de disponibilidade de leitos.
 * 
 * @author gmneto
 * 
 */
public class LeitoDisponibilidadeVO implements BaseBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5898631404354495169L;

	private String leitoId;
	
	private String alaAndar;
	
	private Integer clinicaCodigo;
	
	private String clinicaDescricao;
	
	private String acomodacaoDescricao;
	
	private DominioSexo sexoOcupacao;
	
	private String descricao;
	
	private String descricaoCaracteristica;
	
	private Date dataHoraLancamento;
	
	private Date criadoEm;
	
	private DominioMovimentoLeito grupoMovimentoLeito;
	
	private Short ordemGrupoMovimentoLeito;
	
	private Short numeroQuarto;
	
	private Short unidadeFuncionalId;
	
	private Integer acomodacaoId;
	
	private DominioSexoDeterminante sexoDeterminante;

	/**
	 * @return the leito
	 */
	public String getLeitoId() {
		return leitoId;
	}

	/**
	 * @param leito the leito to set
	 */
	public void setLeitoId(String leito) {
		this.leitoId = leito;
	}

	/**
	 * @return the alaAndar
	 */
	public String getAlaAndar() {
		return alaAndar;
	}

	/**
	 * @param alaAndar the alaAndar to set
	 */
	public void setAlaAndar(String alaAndar) {
		this.alaAndar = alaAndar;
	}

	/**
	 * @return the clinica
	 */
	public Integer getClinicaCodigo() {
		return clinicaCodigo;
	}

	/**
	 * @param clinica the clinica to set
	 */
	public void setClinicaCodigo(Integer clinica) {
		this.clinicaCodigo = clinica;
	}

	/**
	 * @return the acomodacao
	 */
	public String getAcomodacaoDescricao() {
		return acomodacaoDescricao;
	}

	/**
	 * @param acomodacao the acomodacao to set
	 */
	public void setAcomodacaoDescricao(String acomodacao) {
		this.acomodacaoDescricao = acomodacao;
	}

	/**
	 * @return the sexoOcupacao
	 */
	public DominioSexo getSexoOcupacao() {
		return sexoOcupacao;
	}

	/**
	 * @param sexoOcupacao the sexoOcupacao to set
	 */
	public void setSexoOcupacao(DominioSexo sexoOcupacao) {
		this.sexoOcupacao = sexoOcupacao;
	}



	/**
	 * @return the justificativa
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param justificativa the justificativa to set
	 */
	public void setDescricao(String justificativa) {
		this.descricao = justificativa;
	}

	/**
	 * @return the caracteristicas
	 */
	public String getDescricaoCaracteristica() {
		return descricaoCaracteristica;
	}

	/**
	 * @param caracteristicas the caracteristicas to set
	 */
	public void setDescricaoCaracteristica(String caracteristicas) {
		this.descricaoCaracteristica = caracteristicas;
	}

	/**
	 * @return the dataHoraInformada
	 */
	public Date getDataHoraLancamento() {
		return dataHoraLancamento;
	}

	/**
	 * @param dataHoraInformada the dataHoraInformada to set
	 */
	public void setDataHoraLancamento(Date dataHoraInformada) {
		this.dataHoraLancamento = dataHoraInformada;
	}

	/**
	 * @return the lancadaEm
	 */
	public Date getCriadoEm() {
		return criadoEm;
	}

	/**
	 * @param lancadaEm the lancadaEm to set
	 */
	public void setCriadoEm(Date lancadaEm) {
		this.criadoEm = lancadaEm;
	}

	/**
	 * @return the situacao
	 */
	public DominioMovimentoLeito getGrupoMovimentoLeito() {
		return grupoMovimentoLeito;
	}

	/**
	 * @param situacao the situacao to set
	 */
	public void setGrupoMovimentoLeito(DominioMovimentoLeito situacao) {
		this.grupoMovimentoLeito = situacao;
		
		switch (situacao) {
		case L:
			this.setOrdemGrupoMovimentoLeito((short) 1);			
			break;
		case R:
			this.setOrdemGrupoMovimentoLeito((short) 2);			
			break;
		default:
			this.setOrdemGrupoMovimentoLeito((short) 3);
			break;
		}
		
	}

	/**
	 * @return the numeroQuarto
	 */
	public Short getNumeroQuarto() {
		return numeroQuarto;
	}

	/**
	 * @param numeroQuarto the numeroQuarto to set
	 */
	public void setNumeroQuarto(Short numeroQuarto) {
		this.numeroQuarto = numeroQuarto;
	}

	

	/**
	 * @return the unidadeFuncionalId
	 */
	public Short getUnidadeFuncionalId() {
		return unidadeFuncionalId;
	}

	/**
	 * @param unidadeFuncionalId the unidadeFuncionalId to set
	 */
	public void setUnidadeFuncionalId(Short unidadeFuncionalId) {
		this.unidadeFuncionalId = unidadeFuncionalId;
	}

	

	/**
	 * @return the acomodacaoId
	 */
	public Integer getAcomodacaoId() {
		return acomodacaoId;
	}

	/**
	 * @param acomodacaoId the acomodacaoId to set
	 */
	public void setAcomodacaoId(Integer acomodacaoId) {
		this.acomodacaoId = acomodacaoId;
	}

	/**
	 * @return the clinicaDescricao
	 */
	public String getClinicaDescricao() {
		return clinicaDescricao;
	}

	/**
	 * @param clinicaDescricao the clinicaDescricao to set
	 */
	public void setClinicaDescricao(String clinicaDescricao) {
		this.clinicaDescricao = clinicaDescricao;
	}

	/**
	 * @return the ordemGrupoMovimentoLeito
	 */
	public Short getOrdemGrupoMovimentoLeito() {
		return ordemGrupoMovimentoLeito;
	}

	/**
	 * @param ordemGrupoMovimentoLeito the ordemGrupoMovimentoLeito to set
	 */
	public void setOrdemGrupoMovimentoLeito(Short ordemGrupoMovimentoLeito) {
		this.ordemGrupoMovimentoLeito = ordemGrupoMovimentoLeito;
	}

	/**
	 * @return the sexoDeterminante
	 */
	public DominioSexoDeterminante getSexoDeterminante() {
		return sexoDeterminante;
	}

	/**
	 * @param sexoDeterminante the sexoDeterminante to set
	 */
	public void setSexoDeterminante(DominioSexoDeterminante sexoDeterminante) {
		this.sexoDeterminante = sexoDeterminante;
	}
	
	public boolean isSexoOcupacaoMasculino() {
		return DominioSexo.M.equals(this.getSexoOcupacao());
	}

	public boolean isSexoOcupacaoFeminino() {
		return DominioSexo.F.equals(this.getSexoOcupacao());
	}
	
	public boolean isSexoDeterminanteMasculino() {
		return DominioSexoDeterminante.M.equals(this.getSexoDeterminante());		
	}

	public boolean isSexoDeterminanteFeminino() {
		return DominioSexoDeterminante.F.equals(this.getSexoDeterminante());		
	}
	
}
