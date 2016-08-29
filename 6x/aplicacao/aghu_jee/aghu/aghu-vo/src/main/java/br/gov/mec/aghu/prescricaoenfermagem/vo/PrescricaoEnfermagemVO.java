package br.gov.mec.aghu.prescricaoenfermagem.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoEnfermagemVO;

/**
 * 
 * @author diego.pacheco
 *
 */
public class PrescricaoEnfermagemVO implements Serializable, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1932763424213546614L;

	private Integer prontuario;

	private String nome;
	
	private String nomeSocial;

	private String local;

	/**
	 *  epePrescricaoEnfermagem.getDthrInicio();
	 */
	private Date dthrInicio;

	/**
	 * epePrescricaoEnfermagem.getDthrFim();
	 */
	private Date dthrFim;
	
	private EpePrescricaoEnfermagem prescricaoEnfermagem;
	
	private String dataValidadePrescricao;
	
	private Integer sequencialPrescricaoEnfermagem;
	
	private String medicoConfirmacao;
	
	private String nomeMaePaciente;
	
	private Integer ordemTela;
	
	private String prontuarioFormatado;

	private List<CuidadoVO> listaCuidadoVO;
	
	private List<ItemPrescricaoEnfermagemVO> itens = new ArrayList<ItemPrescricaoEnfermagemVO>();

	/**
	 * 
	 */
	public PrescricaoEnfermagemVO() {
		super();
	}

	public PrescricaoEnfermagemVO(EpePrescricaoEnfermagem prescricaoEnfermagem, Integer prontuario, String nome, String local,
			Date dthrInicio, Date dthrFim, Boolean indPacPediatrico,
			Date dtPrevAlta, List<CuidadoVO> itens) {
		this(itens);
		this.prontuario = prontuario;
		this.nome = nome;
		this.local = local;
		this.dthrInicio = dthrInicio;
		this.dthrFim = dthrFim;
	}

	/**
	 * @param listaCuidadoVO
	 */
	public PrescricaoEnfermagemVO(List<CuidadoVO> listaCuidadoVO) {
		this();
		if (listaCuidadoVO == null || listaCuidadoVO.isEmpty()) {
			throw new IllegalArgumentException(
					"A lista de Cuidados de Prescricao de Enfermagem nao pode ser nula ou vazia!!!");
		}
		this.listaCuidadoVO = listaCuidadoVO;
	}

	public void adicionarCuidado(CuidadoVO item) {
		if (item == null || !item.isValid()) {
			throw new IllegalArgumentException("PrescricaoEnfermagemVO: item invalido para adicao na lista.");
		}
		this.getListaCuidadoVO().add(item);
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

	public String getLocal() {
		return this.local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public Date getDthrInicio() {
		return this.dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	/**
	 * epePrescricaoEnfermagem.getDthrFim();
	 */
	public Date getDthrFim() {
		return this.dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	public List<CuidadoVO> getListaCuidadoVO() {
		if (this.listaCuidadoVO == null) {
			this.listaCuidadoVO = new LinkedList<CuidadoVO>();
		}
		return this.listaCuidadoVO;
	}

	public void setListaCuidadoVO(List<CuidadoVO> listaCuidadoVO) {
		this.listaCuidadoVO = listaCuidadoVO;
	}
	
	public PrescricaoEnfermagemVO copiar() {
		try {
			return (PrescricaoEnfermagemVO) this.clone();
		} catch (CloneNotSupportedException e) {
			// engolir exceção nunca vai acontecer pois o bojeto é clonnable.
			return null;
		}
	}

	public EpePrescricaoEnfermagem getPrescricaoEnfermagem() {
		return prescricaoEnfermagem;
	}

	public void setPrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem) {
		this.prescricaoEnfermagem = prescricaoEnfermagem;
	}

	public String getDataValidadePrescricao() {
		return dataValidadePrescricao;
	}

	public void setDataValidadePrescricao(String dataValidadePrescricao) {
		this.dataValidadePrescricao = dataValidadePrescricao;
	}

	public Integer getSequencialPrescricaoEnfermagem() {
		return sequencialPrescricaoEnfermagem;
	}

	public void setSequencialPrescricaoEnfermagem(
			Integer sequencialPrescricaoEnfermagem) {
		this.sequencialPrescricaoEnfermagem = sequencialPrescricaoEnfermagem;
	}

	public String getMedicoConfirmacao() {
		return medicoConfirmacao;
	}

	public void setMedicoConfirmacao(String medicoConfirmacao) {
		this.medicoConfirmacao = medicoConfirmacao;
	}

	public String getNomeMaePaciente() {
		return nomeMaePaciente;
	}

	public void setNomeMaePaciente(String nomeMaePaciente) {
		this.nomeMaePaciente = nomeMaePaciente;
	}

	public Integer getOrdemTela() {
		return ordemTela;
	}

	public void setOrdemTela(Integer ordemTela) {
		this.ordemTela = ordemTela;
	}

	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}

	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}

	public void setItens(List<ItemPrescricaoEnfermagemVO> itens) {
		this.itens = itens;
	}
	
	public List<ItemPrescricaoEnfermagemVO> getItens() {
		return this.itens;
	}

	public void addItem(ItemPrescricaoEnfermagemVO item) {
		if (item == null || !item.isValid()) {
			throw new IllegalArgumentException("ItemPrescricaoEnfermagemVO: item invalido para adicao na lista.");
		}
		this.getItens().add(item);
	}

	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}

}
