/**
 * 
 */
package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * @author rcorvalao
 * 
 */
public class PrescricaoMedicaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5953530542501854336L;

	/**
	 * 
	 */
	
	
	private MpmPrescricaoMedica prescricaoMedica;

	private Integer prontuario;

	private String nome;
	
	private String nomeSocial;

	private String local;

	/**
	 *  mpmPrescricaoMedica.getDthrInicio();
	 */
	private Date dthrInicio;

	/**
	 * mpmPrescricaoMedica.getDthrFim();
	 */
	private Date dthrFim;

	private Boolean indPacPediatrico;

	private Date dtPrevAlta;
	
	/**
	 * Indica se a prescricao medica tem uma Internacao associada ao Atendimento<br>
	 * atraves do relacionamento: <b>prescricaoMedica.atendimento.internacao</b>
	 */
	private Boolean hasInternacao;
	
	private List<ItemPrescricaoMedicaVO> itens;

	/**
	 * 
	 */
	public PrescricaoMedicaVO() {
		super();
	}
	
	public PrescricaoMedicaVO(MpmPrescricaoMedica prescricaoMedica) {
		super();
		this.setPrescricaoMedica(prescricaoMedica);
	}

	public PrescricaoMedicaVO(MpmPrescricaoMedica prescricaoMedica, Integer prontuario, String nome, String nomeSocial, String local,
			Date dthrInicio, Date dthrFim, Boolean indPacPediatrico,
			Date dtPrevAlta, List<ItemPrescricaoMedicaVO> itens) {
		this(itens);
		this.prescricaoMedica = prescricaoMedica;
		this.prontuario = prontuario;
		this.nome = nome;
		this.nomeSocial = nomeSocial;
		this.local = local;
		this.dthrInicio = dthrInicio;
		this.dthrFim = dthrFim;
		this.indPacPediatrico = indPacPediatrico;
		this.dtPrevAlta = dtPrevAlta;
	}

	/**
	 * @param itens
	 */
	public PrescricaoMedicaVO(List<ItemPrescricaoMedicaVO> umListaItens) {
		this();
		if (umListaItens == null || umListaItens.isEmpty()) {
			throw new IllegalArgumentException(
					"A lista de Itens de Prescricao Medica nao pode ser nula ou vazia!!!");
		}
		this.itens = umListaItens;
	}

	public List<ItemPrescricaoMedicaVO> getItens() {
		if (this.itens == null) {
			this.itens = new LinkedList<ItemPrescricaoMedicaVO>();
		}
		return this.itens;
	}

	public void addItem(ItemPrescricaoMedicaVO item) {
		if (item == null || !item.isValid()) {
			throw new IllegalArgumentException("PrescricaoMedicaVO: item invalido para adicao na lista.");
		}
		this.getItens().add(item);
	}

	public String getLocalTrunc(Long size) {
		String retorno;
		if(size == null){
			retorno = StringUtil.trunc(local, true, 15L);
		}else{
			retorno = StringUtil.trunc(local, true, size);
		}
		return retorno;
	}

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}

	public MpmPrescricaoMedicaId getId() {
		return this.prescricaoMedica != null ? this.prescricaoMedica.getId()
				: null;
	}

	public Integer getProntuario() {
		return this.prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}

	public String getLocal() {
		return this.local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public Date getDthrInicio() {
		return this.dthrInicio;
	}

	/**
	 *  mpmPrescricaoMedica.getDthrInicio();
	 * rcorvalao
	 * 05/10/2010
	 * @param dthrInicio
	 */
	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	/**
	 * mpmPrescricaoMedica.getDthrFim();
	 */
	public Date getDthrFim() {
		return this.dthrFim;
	}

	/**
	 * mpmPrescricaoMedica.getDthrFim();
	 * rcorvalao
	 * 05/10/2010
	 * @param dthrFim
	 */
	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	public Boolean getIndPacPediatrico() {
		return this.indPacPediatrico;
	}

	public void setIndPacPediatrico(Boolean indPacPediatrico) {
		this.indPacPediatrico = indPacPediatrico;
	}

	public Date getDtPrevAlta() {
		return this.dtPrevAlta;
	}

	public void setDtPrevAlta(Date dtPrevAlta) {
		this.dtPrevAlta = dtPrevAlta;
	}

	public void setItens(List<ItemPrescricaoMedicaVO> itens) {
		this.itens = itens;
	}

	/**
	 * Indica se a prescricao medica tem uma Internacao associada ao Atendimento<br>
	 * atraves do relacionamento: <b>prescricaoMedica.atendimento.internacao</b><br>
	 * 
	 */
	public void setHasInternacao(Boolean hasInternacao) {
		this.hasInternacao = hasInternacao;
	}

	/**
	 * Indica se a prescricao medica tem uma Internacao associada ao Atendimento<br>
	 * atraves do relacionamento: <b>prescricaoMedica.atendimento.internacao</b><br>
	 * 
	 * @return
	 */
	public Boolean getHasInternacao() {
		return hasInternacao;
	}
	
}
