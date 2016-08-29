package br.gov.mec.aghu.prescricaomedica.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultarRetornoConsultoriaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;

public class ConsultarRetornoConsultoriaController extends ActionController {

	
	private static final long serialVersionUID = 4441757888646314559L;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedica;
	
	@Inject
	private ModalCentralMensagensController modalCentralMensagensController;
	
	private String siglaEspecialidade;
	private String nomeEspecialidade;
	private Integer codProntuario;
	private String nome;
	private String descricao;
	private MpmSolicitacaoConsultoria paramConsultoria;
	
	private ConsultarRetornoConsultoriaVO crcVO;
	
	private static final String PAGE_MANTER_PRESCRICAO_MEDICA = "prescricaomedica-manterPrescricaoMedica";
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar(){
		siglaEspecialidade = null;
		nomeEspecialidade = null;
		codProntuario = null;
		nome = null;
		descricao = "";
		crcVO = null;
		
		if(paramConsultoria != null){
			if (paramConsultoria.getId() != null && paramConsultoria.getId().getAtdSeq() != null) {
				Integer atdSeq = paramConsultoria.getId().getAtdSeq();  
				obterPacienteProntuario(atdSeq);
				obterDescricao(paramConsultoria.getId().getAtdSeq(), paramConsultoria.getId().getSeq());
			}
			
			if (paramConsultoria.getEspecialidade() != null && paramConsultoria.getEspecialidade().getSeq() != null) {
				obterEspecialidade(paramConsultoria.getEspecialidade().getSeq());
			}
		}	
	}
	
	public void obterEspecialidade(Short pEspSeq){
		if(pEspSeq != null){
			AghEspecialidades especialidade = prescricaoMedica.obterSiglaNomeEspecialidadePorEspSeq(pEspSeq);
			nomeEspecialidade = especialidade.getNomeEspecialidade();
			siglaEspecialidade = especialidade.getSigla();
		}	
	}
	
	public void obterPacienteProntuario(Integer pAtdSeq){
		if(pAtdSeq != null){
			ConsultarRetornoConsultoriaVO filtro = prescricaoMedica.obterPacienteNroProntuario(pAtdSeq);
			codProntuario = filtro.getCodProntuario();
			nome = filtro.getNomePaciente();
		}
	}
	
	public void obterDescricao(Integer atdSeq, Integer scnSeq){
		if(atdSeq!=null && scnSeq != null){		
			descricao = prescricaoMedica.pesquisarRespostasConsultoriaPorAtdSeqConsultoria(atdSeq, scnSeq, null);
		}else{
			apresentarMsgNegocio(Severity.INFO, "MSG_RETORNO_CONSULTORIA_NAO_ENCONTRADO_CRC");
				
		}
	}
	
	public String voltar(){
		modalCentralMensagensController.iniciarModal();
		return PAGE_MANTER_PRESCRICAO_MEDICA;
	}
	
	//Getters e setters
	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}
	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}
	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}
	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}
	public Integer getCodProntuario() {
		return codProntuario;
	}
	public void setCodProntuario(Integer codProntuario) {
		this.codProntuario = codProntuario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ConsultarRetornoConsultoriaVO getCrcVO() {
		return crcVO;
	}

	public void setCrcVO(ConsultarRetornoConsultoriaVO crcVO) {
		this.crcVO = crcVO;
	}

	public MpmSolicitacaoConsultoria getParamConsultoria() {
		return paramConsultoria;
	}

	public void setParamConsultoria(MpmSolicitacaoConsultoria paramConsultoria) {
		this.paramConsultoria = paramConsultoria;
	}
}
