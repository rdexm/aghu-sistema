package br.gov.mec.aghu.internacao.responsaveispaciente.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.dominio.DominioTipoResponsabilidade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.internacao.dao.AinResponsaveisPacienteDAO;
import br.gov.mec.aghu.internacao.dao.AinResponsaveisPacienteJnDAO;
import br.gov.mec.aghu.model.AghResponsavel;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.model.AinResponsaveisPacienteJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * O propósito desta classe é prover os métodos de negócio para o cadastro de	
 * responsáveis pelo paciente.
 */
@Stateless
public class ResponsaveisPacienteON extends BaseBusiness {


	@EJB
	private ResponsaveisPacienteRN responsaveisPacienteRN;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private static final Log LOG = LogFactory.getLog(ResponsaveisPacienteON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
		
	@Inject
	private AinResponsaveisPacienteDAO ainResponsaveisPacienteDAO;
	
	@Inject
	private AinResponsaveisPacienteJnDAO ainResponsaveisPacienteJnDAO;


	 /**
	 * 
	 */
	private static final long serialVersionUID = 413039176063971860L;

	/**
	   * Enumeracao com os codigos de mensagens de excecoes negociais do cadastro
	   * paciente.
	   * 
	   * Cada entrada nesta enumeracao deve corresponder a um chave no message
	   * bundle de paciente.
	   * 
	   * Porém se não houver uma este valor será enviado como mensagem de execeção
	   * sem localização alguma.
	   * 
	   * 
	   */
     private enum ResponsaveisPacienteONExceptionCode implements BusinessExceptionCode {
             AIN_PACIENTE_MAIOR_DE_DEZOITO, AIN_TIPO_RESPONSABILIDADE_CONTA,
             AIN_NOME_OBRIGATORIO, AIN_TIPO_RESPONSABILIDADE_OBRIGATORIA, ERRO_REMOVER_RESPONSAVEIS_PACIENTES
     }
     
     /**
	  * Método que verifica os dados obrigatórios
	  * 
	  * @param responsavelPaciente
	  * @throws ApplicationBusinessException
	  */
     public void validarDadosObrigatorios(AinResponsaveisPaciente responsavelPaciente) throws ApplicationBusinessException{
    	 //Mudança para lançar exceção sem rollback gmneto 12/02/2010
    	 if (StringUtils.isBlank(responsavelPaciente.getNome())){
    		 throw new ApplicationBusinessException(ResponsaveisPacienteONExceptionCode.AIN_NOME_OBRIGATORIO);
    	 }
    	 if (responsavelPaciente.getTipoResponsabilidade() == null){
    		 throw new ApplicationBusinessException(ResponsaveisPacienteONExceptionCode.AIN_TIPO_RESPONSABILIDADE_OBRIGATORIA);
    	 }
     }
     
     /**
      * Método que valida as regras de responsáveis de paciente
      * @param listaResponsaveisPaciente
      * @param listaResponsaveisPacienteExcluidos
      * @param internacao
      * @throws ApplicationBusinessException
      */
     public void validarRegrasResponsaveisPaciente(List<AinResponsaveisPaciente> listaResponsaveisPaciente,
			 AinInternacao internacao) throws ApplicationBusinessException{
    	 //Mudança para lançar exceção sem rollback gmneto 12/02/2010
    	 int contadorResponsabilidadesConta = 0;
    	 
    	 for (AinResponsaveisPaciente responsavelPaciente: listaResponsaveisPaciente){
    		 this.validarDadosObrigatorios(responsavelPaciente);
    		 if (responsavelPaciente.getTipoResponsabilidade().equals(DominioTipoResponsabilidade.C)
				 || responsavelPaciente.getTipoResponsabilidade().equals(DominioTipoResponsabilidade.CT)){
				 contadorResponsabilidadesConta++;
			 }
			 if (contadorResponsabilidadesConta > 1){
				 throw new ApplicationBusinessException(ResponsaveisPacienteONExceptionCode.AIN_TIPO_RESPONSABILIDADE_CONTA);
			 }
    	 }
    	 //Deve haver ao menos 1 responsável do tipo conta para pacientes menores de 18 anos
    	 if (contadorResponsabilidadesConta == 0 && internacao.getPaciente().getIdade() < 18){
    		 throw new ApplicationBusinessException(ResponsaveisPacienteONExceptionCode.AIN_TIPO_RESPONSABILIDADE_CONTA);
    	 }
    	 
     }
     
     /**
	  * Método responsável pela atualização da lista de responsáveis pelo paciente.
	  * 
	  * @param listaResponsaveisPaciente
	  * @throws ApplicationBusinessException
	  */
	 public void atualizarListaResponsaveisPaciente(List<AinResponsaveisPaciente> listaResponsaveisPaciente,
			 List<AinResponsaveisPaciente> listaResponsaveisPacienteExcluidos, AinInternacao internacao
			 , List<AinResponsaveisPaciente> listResponsavelPacienteOld) throws ApplicationBusinessException {

			for (AinResponsaveisPaciente responsavelPaciente : listaResponsaveisPacienteExcluidos) {
				if (responsavelPaciente.getSeq() != null) {
					this.removerResponsavelPaciente(responsavelPaciente);
				}
			}
			this.flush();
	 
	 
			for (int i = 0; i < listaResponsaveisPaciente.size(); i++) {
				AinResponsaveisPaciente responsavelPaciente = listaResponsaveisPaciente.get(i);
	
				passarValoresResponsavelCaixaAlta(responsavelPaciente);
				if (responsavelPaciente.getSeq() == null) {
					responsavelPaciente.setInternacao(internacao);
					// inclusão
					this.incluirResponsavelPaciente(responsavelPaciente);
				} else {
					// edição
	
					this.atualizarResponsavelPaciente(responsavelPaciente, listResponsavelPacienteOld.get(i));
				}
				this.flush();
			}
		 }
		
	 
     
     
     /**	
      * Método que passa os valores String de responsavel paciente para maiúsculo
      * 
      * @param responsavelPaciente
      */
     public void passarValoresResponsavelCaixaAlta(AinResponsaveisPaciente responsavelPaciente){
    	 responsavelPaciente.setNome(responsavelPaciente.getNome().toUpperCase());
    	 if (StringUtils.isNotBlank(responsavelPaciente.getNomeMae())){
    		 responsavelPaciente.setNomeMae(responsavelPaciente.getNomeMae().toUpperCase());
    	 }
    	 if (StringUtils.isNotBlank(responsavelPaciente.getCidade())){
    		 responsavelPaciente.setCidade(responsavelPaciente.getCidade().toUpperCase());
    	 }
    	 if (StringUtils.isNotBlank(responsavelPaciente.getLogradouro())){
    		 responsavelPaciente.setLogradouro(responsavelPaciente.getLogradouro().toUpperCase());
    	 }
    	 if (StringUtils.isNotBlank(responsavelPaciente.getOrgaoEmisRg())){
    		 responsavelPaciente.setOrgaoEmisRg(responsavelPaciente.getOrgaoEmisRg().toUpperCase());
    	 }
     }
     
     /**	
     * Método responsável por incluir um novo responsável pelo paciente.
     * 
     * @param responsavelPaciente
     *  
     */
     private void incluirResponsavelPaciente(AinResponsaveisPaciente responsavelPaciente) throws ApplicationBusinessException {
    	 ResponsaveisPacienteRN responsaveisPacienteRN = this.getResponsaveisPacienteRN();
    	 
    	 responsaveisPacienteRN.validarDadosResponsavelPaciente(responsavelPaciente);
    	 responsaveisPacienteRN.incluirResponsavelPaciente(responsavelPaciente);
    	 
    	 AinResponsaveisPacienteDAO ainResponsaveisPacienteDAO = this.getAinResponsaveisPacienteDAO();
    	 ainResponsaveisPacienteDAO.persistir(responsavelPaciente);
     }


     /**
     * Método responsável por atualizar um novo responsável pelo paciente.
     * 
     * @param responsavelPaciente
     *  
     */
     private void atualizarResponsavelPaciente(AinResponsaveisPaciente responsavelPaciente,AinResponsaveisPaciente responsavelPacienteOld) throws ApplicationBusinessException {
    	 AinResponsaveisPacienteDAO ainResponsaveisPacienteDAO = this.getAinResponsaveisPacienteDAO();		 
    	
    	 if (!ainResponsaveisPacienteDAO.contains(responsavelPaciente)){
    		 responsavelPaciente = ainResponsaveisPacienteDAO.merge(responsavelPaciente);
    	 }

		 this.getResponsaveisPacienteRN().atualizarResponsavelPaciente(responsavelPaciente);
		 updatePacienteInternacaoJn(responsavelPaciente,responsavelPacienteOld,responsavelPaciente.getServidor());
     }
     
     
	 /**
	  * Método responsável por remover um responsável pelo paciente.
	  * 
	  * @param responsavelPaciente
	 *  
	  */
 	private void removerResponsavelPaciente(AinResponsaveisPaciente responsavelPaciente) throws ApplicationBusinessException {
		AinResponsaveisPacienteDAO ainResponsaveisPacienteDAO = this.getAinResponsaveisPacienteDAO();

		if (!ainResponsaveisPacienteDAO.contains(responsavelPaciente)) {
			responsavelPaciente = ainResponsaveisPacienteDAO.merge(responsavelPaciente);
		}
		this.getResponsaveisPacienteRN().removerResponsavelPaciente(responsavelPaciente);
		ainResponsaveisPacienteDAO.remover(responsavelPaciente);
		ainResponsaveisPacienteDAO.flush();
		
		criarAinResponsaveisPacienteJn(responsavelPaciente, DominioOperacoesJournal.DEL);
		this.cadastrosApoioExamesFacade.criarAghResponsavelJn(responsavelPaciente.getResponsavelConta(), DominioOperacoesJournal.DEL);
	}
 	
	private void updatePacienteInternacaoJn(AinResponsaveisPaciente responsavelPaciente, AinResponsaveisPaciente responsavelPacienteOld, RapServidores servidorLogado){
		if (CoreUtil.modificados(responsavelPaciente.getNome(), responsavelPacienteOld.getNome())
				|| CoreUtil.modificados(responsavelPaciente.getCpf(), responsavelPacienteOld.getCpf())
				|| CoreUtil.modificados(responsavelPaciente.getSeq(), responsavelPacienteOld.getSeq())
				|| CoreUtil.modificados(responsavelPaciente.getTipoResponsabilidade(), responsavelPacienteOld.getTipoResponsabilidade())
				|| CoreUtil.modificados(responsavelPaciente.getLogradouro(), responsavelPacienteOld.getLogradouro())
				|| CoreUtil.modificados(responsavelPaciente.getCidade(), responsavelPacienteOld.getCidade())
				|| CoreUtil.modificados(responsavelPaciente.getUf(), responsavelPacienteOld.getUf())
				|| CoreUtil.modificados(responsavelPaciente.getCep(), responsavelPacienteOld.getCep())
				|| CoreUtil.modificados(responsavelPaciente.getDddFone(), responsavelPacienteOld.getDddFone())
				|| CoreUtil.modificados(responsavelPaciente.getFone(), responsavelPacienteOld.getFone())
				|| CoreUtil.modificados(responsavelPaciente.getRegNascimento(), responsavelPacienteOld.getRegNascimento())
				|| CoreUtil.modificados(responsavelPaciente.getNroCartaoSaude(), responsavelPacienteOld.getNroCartaoSaude())
				|| CoreUtil.modificados(responsavelPaciente.getRg(), responsavelPacienteOld.getRg())
				|| CoreUtil.modificados(responsavelPaciente.getOrgaoEmisRg(), responsavelPacienteOld.getOrgaoEmisRg())
				|| CoreUtil.modificados(responsavelPaciente.getNomeMae(), responsavelPacienteOld.getNomeMae())
				|| CoreUtil.modificados(responsavelPaciente.getAtendimento(), responsavelPacienteOld.getAtendimento())
				|| CoreUtil.modificados(responsavelPaciente.getCntaConv(), responsavelPacienteOld.getCntaConv())
				|| CoreUtil.modificados(responsavelPaciente.getEmail(), responsavelPacienteOld.getEmail())
				|| CoreUtil.modificados(responsavelPaciente.getEmailPaciente(), responsavelPacienteOld.getEmailPaciente())
				|| CoreUtil.modificados(responsavelPaciente.getServidor().getMatriculaVinculo(), responsavelPacienteOld.getServidor().getMatriculaVinculo())
				|| CoreUtil.modificados(responsavelPaciente.getServidor().getVinculo().getCodigo(), responsavelPacienteOld.getServidor().getVinculo().getCodigo())
				|| CoreUtil.modificados(responsavelPaciente.getDtNascimento(), responsavelPacienteOld.getDtNascimento())
				|| CoreUtil.modificados(responsavelPaciente.getResponsavelConta(), responsavelPacienteOld.getResponsavelConta())){
			
			
			criarAinResponsaveisPacienteJn(responsavelPacienteOld ,DominioOperacoesJournal.UPD);
		
		}
	}
	
	private void criarAinResponsaveisPacienteJn(AinResponsaveisPaciente responsavelPaciente, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		final AinResponsaveisPacienteJn ainResponsaveisPacienteJn = BaseJournalFactory.getBaseJournal(
				operacao
				, AinResponsaveisPacienteJn.class
				, servidorLogado.getUsuario());
		
		populaSeqAtendimentoSeqUf(responsavelPaciente, ainResponsaveisPacienteJn);
		populaAtendimentoCntaConvResponsavel(responsavelPaciente, ainResponsaveisPacienteJn);
		ainResponsaveisPacienteJn.setTipoResponsabilidade(responsavelPaciente.getTipoResponsabilidade());
		ainResponsaveisPacienteJn.setSerMatricula(servidorLogado.getServidor().getId().getMatricula());
		ainResponsaveisPacienteJn.setSerVinCodigo(servidorLogado.getServidor().getId().getVinCodigo());
		ainResponsaveisPacienteJn.setFone(responsavelPaciente.getFone());
		ainResponsaveisPacienteJn.setNome(responsavelPaciente.getNome());
		ainResponsaveisPacienteJn.setCpf(responsavelPaciente.getCpf());
		ainResponsaveisPacienteJn.setLogradouro(responsavelPaciente.getLogradouro());
		ainResponsaveisPacienteJn.setCidade(responsavelPaciente.getCidade());
		ainResponsaveisPacienteJn.setCep(responsavelPaciente.getCep());
		ainResponsaveisPacienteJn.setDddFone(responsavelPaciente.getDddFone());
		ainResponsaveisPacienteJn.setRegNascimento(responsavelPaciente.getRegNascimento());
		ainResponsaveisPacienteJn.setNroCartaoSaude(responsavelPaciente.getNroCartaoSaude());
		ainResponsaveisPacienteJn.setRg(responsavelPaciente.getRg());
		ainResponsaveisPacienteJn.setOrgaoEmisRg(responsavelPaciente.getOrgaoEmisRg());
		ainResponsaveisPacienteJn.setNomeMae(responsavelPaciente.getNomeMae());
		ainResponsaveisPacienteJn.setEmail(responsavelPaciente.getEmail());
		ainResponsaveisPacienteJn.setEmailPaciente(responsavelPaciente.getEmailPaciente());
		ainResponsaveisPacienteJn.setDtNascimento(responsavelPaciente.getDtNascimento());
	
		this.getAinResponsaveisPacienteJnDAO().persistir(ainResponsaveisPacienteJn);
		this.flush();
	}

	private void populaSeqAtendimentoSeqUf(
			AinResponsaveisPaciente responsavelPaciente,
			final AinResponsaveisPacienteJn ainResponsaveisPacienteJn) {
		ainResponsaveisPacienteJn.setSeqInternacao((responsavelPaciente.getInternacao() != null) ? responsavelPaciente.getInternacao().getSeq() : null);
		ainResponsaveisPacienteJn.setSeq(responsavelPaciente != null ? responsavelPaciente.getSeq() : null);
		ainResponsaveisPacienteJn.setUf(responsavelPaciente.getUf() != null ? responsavelPaciente.getUf().getSigla() : null);
	}
	
	private void populaAtendimentoCntaConvResponsavel(
			AinResponsaveisPaciente responsavelPaciente,
			final AinResponsaveisPacienteJn ainResponsaveisPacienteJn) {
		ainResponsaveisPacienteJn.setAtendimento(responsavelPaciente.getAtendimento() != null ? responsavelPaciente.getAtendimento().getSeq() : null);
		ainResponsaveisPacienteJn.setCntaConv(responsavelPaciente.getCntaConv() != null ? responsavelPaciente.getCntaConv().getNro() : null);
		ainResponsaveisPacienteJn.setResponsavelConta(responsavelPaciente.getResponsavelConta() != null ? responsavelPaciente.getResponsavelConta().getSeq() : null);
	}
	  
	  /**
	   * ORADB Procedure ainp_delete_rep_rows
	   * Métoro responsável por remover responsáveis de um paciente
	   * Método sincronizado devido ao semáforo na procedure
	   * @param responsaveisPaciente
	   * @throws ApplicationBusinessException
	   */
	  public synchronized void removerResponsaveisPacienteInternacao(List<AinResponsaveisPaciente> responsaveisPaciente) throws ApplicationBusinessException{
		AinResponsaveisPacienteDAO ainResponsaveisPacienteDAO = this.getAinResponsaveisPacienteDAO();

		try {
			if (responsaveisPaciente.size() > 0) {
				ResponsaveisPacienteRN responsaveisPacienteRN = this.getResponsaveisPacienteRN();

				for (AinResponsaveisPaciente responsavel : responsaveisPaciente) {
					AinResponsaveisPaciente item = ainResponsaveisPacienteDAO.obterPorChavePrimaria(responsavel.getSeq());
					responsaveisPacienteRN.removerResponsavelPaciente(item);
					ainResponsaveisPacienteDAO.remover(item);
				}
				ainResponsaveisPacienteDAO.flush();
			}

		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			Throwable cause = ExceptionUtils.getCause(e);
			if (cause instanceof ConstraintViolationException) {
				throw new ApplicationBusinessException(ResponsaveisPacienteONExceptionCode.ERRO_REMOVER_RESPONSAVEIS_PACIENTES,
						((ConstraintViolationException) cause).getConstraintName());
			} else {
				throw new ApplicationBusinessException(ResponsaveisPacienteONExceptionCode.ERRO_REMOVER_RESPONSAVEIS_PACIENTES, "");
			}
		}
	}

  	/**
     * Método que obtém o número de responsáveis por um paciente para uma
     * certa internação
     * 
     * @param intSeq
     * @return listaResponsaveisPaciente
     */
	public List<AinResponsaveisPaciente> pesquisarResponsaveisPaciente(Integer intSeq) {
		return this.getAinResponsaveisPacienteDAO().pesquisarResponsaveisPaciente(intSeq);
	}
  	
  	/**
     * Método que obtém o responsável do paciente contratante 
     * 
     * @param intSeq
     * @return AinResponsaveisPaciente
     */
 	public AinResponsaveisPaciente obterResponsaveisPacienteTipoConta(Integer intSeq){
  		return this.getAinResponsaveisPacienteDAO().obterResponsaveisPacienteTipoConta(intSeq);
  	}
 	
  	/**
     * Método que obtém o nome do responsável do paciente contratante 
     * 
     * @param intSeq
     * @return AinResponsaveisPaciente
     */
 	public String obterNomeResponsavelPacienteTipoConta(Integer intSeq){
 		String respNome = "";
 		
  		AinResponsaveisPaciente responsavel = getAinResponsaveisPacienteDAO().obterResponsaveisPacienteTipoConta(intSeq);
  		if(responsavel!=null) {
  			if(responsavel.getResponsavelConta()!=null) {
  				AghResponsavel aghResp = this.cadastrosApoioExamesFacade.obterResponsavelPorSeq(responsavel.getResponsavelConta().getSeq());
  				if(aghResp.getAipPaciente()!=null) {
  					respNome = aghResp.getAipPaciente().getNome();
  				}
  				else {
  					respNome = aghResp.getNome();
  				}
  			}
  			else {
  				respNome = responsavel.getNome();
  			}
  		}
  		return respNome;
  	}

	protected ResponsaveisPacienteRN getResponsaveisPacienteRN() {
		return responsaveisPacienteRN;
	}
	
	protected AinResponsaveisPacienteDAO getAinResponsaveisPacienteDAO() {
		return ainResponsaveisPacienteDAO;
	}

	protected AinResponsaveisPacienteJnDAO getAinResponsaveisPacienteJnDAO() {
		return ainResponsaveisPacienteJnDAO;
	}

	protected ICadastrosApoioExamesFacade getCadastrosApoioExamesFacade() {
		return cadastrosApoioExamesFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
	
}
