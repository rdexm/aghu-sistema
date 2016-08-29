package br.gov.mec.aghu.internacao.leitos.action;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;


public class VerificarCaracteristicasPedidoInternacaoController extends ActionController {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4977160635679587719L;

	@EJB
	private ILeitosInternacaoFacade leitosInternacaoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
			
	private AinSolicTransfPacientes solicTransfPaciente;
	
	private String dataSolicitacao;
	private Integer codPaciente;
	private String prontuario;
	private String nomePaciente;
	private String acomodacao;
	private String descricaoAcomodacao;
	private String especialidade;
	private String descricaoEspecialidade;
	private String crmProfessor;
	private String nomeProfessorResponsavel;
	private String observacoes;
	private String dtAtendimento;
	private String dtDeAlta;
	private Boolean leitoIsolamento;
	private Integer codVinculoServidorSolicitante;
	private Integer matriculaServidorSolicitante;
	private String nomeServidorSolicitante;
	private Integer codVinculoServidorDigitador;
	private Integer matriculaServidorDigitador;
	private String nomeServidorDigitador;
	private Integer codVinculoServidorCancelador;
	private Integer matriculaServidorCancelador;
	private String nomeServidorCancelador;
	
	EspCrmVO espCrmVO;
	
	@PostConstruct
	protected void init() {
		begin(conversation, true);
	}
	
	public void inicio(){
				
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getServidorProfessor()!= null){
			List<EspCrmVO> lista  = leitosInternacaoFacade.obterDadosDoMedicoPelaMatriculaEVinCodigo(this.solicTransfPaciente.getServidorProfessor().getId().getMatricula(), this.solicTransfPaciente.getServidorProfessor().getId().getVinCodigo());
			if(lista != null && lista.size() > 0){
				this.espCrmVO = lista.get(0);
			}
		}
		
	}
	
		
	public String cancelar(){
	
		return "cancelar";
	}


	public String getDataSolicitacao() {
		if(this.solicTransfPaciente != null ){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			this.dataSolicitacao  = sdf.format(this.solicTransfPaciente.getCriadoEm());
		}
		return dataSolicitacao;
	}

	public Integer getCodPaciente() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getInternacao() != null){
			this.codPaciente = this.solicTransfPaciente.getInternacao().getPaciente().getCodigo();
		}
		return codPaciente;
	}


	public String getProntuario() {
		String str = null;
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getInternacao() != null && this.solicTransfPaciente.getInternacao().getPaciente().getProntuario() != null){
			str = this.solicTransfPaciente.getInternacao().getPaciente().getProntuario().toString();
			this.prontuario = str.substring(0, str.length()-1 ) + "/" + str.substring(str.length()-1, str.length());
		}
		return prontuario;
	}


	public String getNomePaciente() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getInternacao() != null){
			this.nomePaciente = this.solicTransfPaciente.getInternacao().getPaciente().getNome();
		}
		return nomePaciente;
	}


	public String getAcomodacao() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getAcomodacoes() != null){
			this.acomodacao = this.solicTransfPaciente.getAcomodacoes().getSeq().toString();
		}
		return acomodacao;
	}

	public String getDescricaoAcomodacao() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getAcomodacoes() != null){
			this.descricaoAcomodacao = this.solicTransfPaciente.getAcomodacoes().getDescricao();
		}
		return descricaoAcomodacao;
	}


	public String getEspecialidade() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getEspecialidades() != null){
			this.especialidade = this.solicTransfPaciente.getEspecialidades().getSigla();
		}
		return especialidade;
	}


	public String getDescricaoEspecialidade() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getEspecialidades() != null){
			this.descricaoEspecialidade = this.solicTransfPaciente.getEspecialidades().getNomeEspecialidade();
		}

		return descricaoEspecialidade;
	}


	public String getCrmProfessor() {
		if(this.espCrmVO != null ){
			this.crmProfessor = espCrmVO.getNroRegConselho();
		}
		return crmProfessor;
	}


	public String getNomeProfessorResponsavel() {
		if(this.espCrmVO != null ){
			this.nomeProfessorResponsavel = espCrmVO.getNomeMedico();
		}
		return nomeProfessorResponsavel;
	}


	public String getObservacoes() {
		if(this.solicTransfPaciente != null ){
			this.observacoes = this.solicTransfPaciente.getObservacao();
		}
		return observacoes;
	}


	public String getDtAtendimento() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getDthrAtendimentoSolicitacao() != null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			this.dtAtendimento = sdf.format(this.solicTransfPaciente.getDthrAtendimentoSolicitacao());
		}
		return dtAtendimento;
	}


	public Boolean getLeitoIsolamento() {
		if(this.solicTransfPaciente != null && DominioSimNao.S.equals(this.solicTransfPaciente.getIndLeitoIsolamento())){
			this.leitoIsolamento = true;	
		}else{
			this.leitoIsolamento = false;	
		}
		return leitoIsolamento;
	}

	

	public String getDtDeAlta() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getInternacao()!= null && this.solicTransfPaciente.getInternacao().getDthrAltaMedica()!= null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			this.dtDeAlta = sdf.format(this.solicTransfPaciente.getInternacao().getDthrAltaMedica());
		}
		return dtDeAlta;
	}


	public Integer getCodVinculoServidorSolicitante() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getServidorSolicitante() != null ){
			this.codVinculoServidorSolicitante = this.solicTransfPaciente.getServidorSolicitante().getId().getVinCodigo().intValue();	
		}
		return codVinculoServidorSolicitante;
	}


	public Integer getMatriculaServidorSolicitante() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getServidorSolicitante() != null ){
			this.matriculaServidorSolicitante = this.solicTransfPaciente.getServidorSolicitante().getId().getMatricula();	
		}
		return matriculaServidorSolicitante;
	}


	public String getNomeServidorSolicitante() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getServidorSolicitante() != null ){
			this.nomeServidorSolicitante = registroColaboradorFacade.obterNomePessoaServidor(solicTransfPaciente.getServidorSolicitante().getId().getVinCodigo(), solicTransfPaciente.getServidorSolicitante().getId().getMatricula());
		}
		return nomeServidorSolicitante;
	}


	public Integer getCodVinculoServidorDigitador() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getServidorDigitador() != null ){
			this.codVinculoServidorDigitador = this.solicTransfPaciente.getServidorDigitador().getId().getVinCodigo().intValue();	
		}
		return codVinculoServidorDigitador;
	}

	public Integer getMatriculaServidorDigitador() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getServidorDigitador() != null ){
			this.matriculaServidorDigitador = this.solicTransfPaciente.getServidorDigitador().getId().getMatricula();	
		}
		return matriculaServidorDigitador;
	}


	public String getNomeServidorDigitador() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getServidorDigitador() != null ){
			this.nomeServidorDigitador = registroColaboradorFacade.obterNomePessoaServidor(solicTransfPaciente.getServidorDigitador().getId().getVinCodigo(), solicTransfPaciente.getServidorDigitador().getId().getMatricula());
		}
		return nomeServidorDigitador;
	}


	public Integer getCodVinculoServidorCancelador() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getServidorCancelador() != null ){
			this.codVinculoServidorCancelador = this.solicTransfPaciente.getServidorCancelador().getId().getVinCodigo().intValue();	
		}
		return codVinculoServidorCancelador;
	}


	public Integer getMatriculaServidorCancelador() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getServidorCancelador() != null ){
			this.matriculaServidorCancelador = this.solicTransfPaciente.getServidorCancelador().getId().getMatricula();	
		}
		return matriculaServidorCancelador;
	}


	public String getNomeServidorCancelador() {
		if(this.solicTransfPaciente != null && this.solicTransfPaciente.getServidorCancelador() != null ){
			this.nomeServidorCancelador = registroColaboradorFacade.obterNomePessoaServidor(solicTransfPaciente.getServidorCancelador().getId().getVinCodigo(), solicTransfPaciente.getServidorCancelador().getId().getMatricula());
		}
		return nomeServidorCancelador;
	}

	public AinSolicTransfPacientes getSolicTransfPaciente() {
		return solicTransfPaciente;
	}

	public void setSolicTransfPaciente(AinSolicTransfPacientes solicTransfPaciente) {
		this.solicTransfPaciente = solicTransfPaciente;
	}

	public EspCrmVO getEspCrmVO() {
		return espCrmVO;
	}

	public void setEspCrmVO(EspCrmVO espCrmVO) {
		this.espCrmVO = espCrmVO;
	}

	public void setDataSolicitacao(String dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public void setAcomodacao(String acomodacao) {
		this.acomodacao = acomodacao;
	}

	public void setDescricaoAcomodacao(String descricaoAcomodacao) {
		this.descricaoAcomodacao = descricaoAcomodacao;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public void setDescricaoEspecialidade(String descricaoEspecialidade) {
		this.descricaoEspecialidade = descricaoEspecialidade;
	}

	public void setCrmProfessor(String crmProfessor) {
		this.crmProfessor = crmProfessor;
	}

	public void setNomeProfessorResponsavel(String nomeProfessorResponsavel) {
		this.nomeProfessorResponsavel = nomeProfessorResponsavel;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public void setDtAtendimento(String dtAtendimento) {
		this.dtAtendimento = dtAtendimento;
	}

	public void setDtDeAlta(String dtDeAlta) {
		this.dtDeAlta = dtDeAlta;
	}

	public void setLeitoIsolamento(Boolean leitoIsolamento) {
		this.leitoIsolamento = leitoIsolamento;
	}

	public void setCodVinculoServidorSolicitante(
			Integer codVinculoServidorSolicitante) {
		this.codVinculoServidorSolicitante = codVinculoServidorSolicitante;
	}

	public void setMatriculaServidorSolicitante(Integer matriculaServidorSolicitante) {
		this.matriculaServidorSolicitante = matriculaServidorSolicitante;
	}

	public void setNomeServidorSolicitante(String nomeServidorSolicitante) {
		this.nomeServidorSolicitante = nomeServidorSolicitante;
	}

	public void setCodVinculoServidorDigitador(Integer codVinculoServidorDigitador) {
		this.codVinculoServidorDigitador = codVinculoServidorDigitador;
	}

	public void setMatriculaServidorDigitador(Integer matriculaServidorDigitador) {
		this.matriculaServidorDigitador = matriculaServidorDigitador;
	}

	public void setNomeServidorDigitador(String nomeServidorDigitador) {
		this.nomeServidorDigitador = nomeServidorDigitador;
	}

	public void setCodVinculoServidorCancelador(Integer codVinculoServidorCancelador) {
		this.codVinculoServidorCancelador = codVinculoServidorCancelador;
	}

	public void setMatriculaServidorCancelador(Integer matriculaServidorCancelador) {
		this.matriculaServidorCancelador = matriculaServidorCancelador;
	}

	public void setNomeServidorCancelador(String nomeServidorCancelador) {
		this.nomeServidorCancelador = nomeServidorCancelador;
	}
	
}
