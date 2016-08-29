package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipGrupoFamiliar;
import br.gov.mec.aghu.model.AipGrupoFamiliarJn;
import br.gov.mec.aghu.model.AipGrupoFamiliarPacientes;
import br.gov.mec.aghu.model.AipGrupoFamiliarPacientesJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.dao.AipGrupoFamiliarDAO;
import br.gov.mec.aghu.paciente.dao.AipGrupoFamiliarJnDAO;
import br.gov.mec.aghu.paciente.dao.AipGrupoFamiliarPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipGrupoFamiliarPacientesJnDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AgrupamentoFamiliarON extends BaseBusiness{

	/**
	 * #42462 - Agrupamento de Pacientes
	 */
	private static final long serialVersionUID = 6071001312076463731L;
	private static final Log LOG = LogFactory.getLog(AgrupamentoFamiliarON.class);
	@Inject
	private AipGrupoFamiliarDAO grupoFamiliarDAO; 
	@Inject
	private AipPacientesDAO pacienteDAO;
	@Inject
	private AipGrupoFamiliarPacientesDAO aipGrupoFamiliarPacientesDAO;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	@Inject
	private AipGrupoFamiliarJnDAO aipGrupoFamiliarJnDAO;
	@Inject
	private AipGrupoFamiliarPacientesJnDAO aipGrupoFamiliarPacientesJnDAO;
	
	public enum AgrupamentoFamiliarONExceptionCode implements BusinessExceptionCode { 
		MENSAGEM_PRONTUARIO_FAMILIA_EXISTENTE;
	}

	/**
	 * RN03
	 * @param pacCodigo
	 * @throws ApplicationBusinessException
	 */
	public void desvincularPacienteGrupoFamiliar(Integer pacCodigo) throws ApplicationBusinessException{
		RapServidores servidorLogado = registroColaboradorFacade.obterServidorPorUsuario(obterLoginUsuarioLogado());
		AipGrupoFamiliarPacientes grupoFamiliarPaciente = aipGrupoFamiliarPacientesDAO.obterPorChavePrimaria(pacCodigo);
		
		AipGrupoFamiliar grupoFamiliar = grupoFamiliarDAO.obterPorChavePrimaria(grupoFamiliarPaciente.getAgfSeq());
		grupoFamiliar.setServidor(servidorLogado);
		grupoFamiliar.setAlteradoEm(new Date());
	
		grupoFamiliarDAO.atualizar(grupoFamiliar);
		aipGrupoFamiliarPacientesDAO.remover(grupoFamiliarPaciente);
		
		
		AipGrupoFamiliarPacientesJn jn = BaseJournalFactory.getBaseJournal(
				DominioOperacoesJournal.DEL, AipGrupoFamiliarPacientesJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		jn.setPacCodigo(pacCodigo);
		jn.setAgfSeq(grupoFamiliarPaciente.getAgfSeq());
		aipGrupoFamiliarPacientesJnDAO.persistir(jn); 
		
		
		AipGrupoFamiliarJn jn3 = BaseJournalFactory.getBaseJournal(
				DominioOperacoesJournal.DEL, AipGrupoFamiliarJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		jn3.setSeq(grupoFamiliarPaciente.getAgfSeq());
		jn3.setSerMatricula(servidorLogado.getId().getMatricula());
		jn3.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		jn3.setAlteradoEm(grupoFamiliar.getAlteradoEm());
		jn3.setCriadoEm(grupoFamiliar.getCriadoEm());
		aipGrupoFamiliarJnDAO.persistir(jn3);
		
		
		

	}
	/**
	 * RN03 
	 * (Documentação) - *Importante: Se o usuário selecionar um paciente que já possui um protnuario familia...Executar fluxo de desativação e apos realizar inserção 
	 * OBS do metodo: (remover e incluir) dessa forma foi necessário utilizar flush
	 * @param pacientePesquisado
	 * @param agfSeq
	 * @throws ApplicationBusinessException
	 */
	public void atualizarPacientePesquisadoGrupoFamiliar(Integer pacientePesquisado,Integer agfSeq) throws ApplicationBusinessException{
		RapServidores servidorLogado = registroColaboradorFacade.obterServidorPorUsuario(obterLoginUsuarioLogado());
		AipGrupoFamiliarPacientes grupoFamiliarPaciente = aipGrupoFamiliarPacientesDAO.obterPorChavePrimaria(pacientePesquisado);
		grupoFamiliarPaciente.getPaciente().setGrupoFamiliarPaciente(null);
		aipGrupoFamiliarPacientesDAO.remover(grupoFamiliarPaciente);
		aipGrupoFamiliarPacientesDAO.flush();
		
		AipGrupoFamiliarPacientes grupoFamiliarPacienteNovo = new AipGrupoFamiliarPacientes();
		grupoFamiliarPacienteNovo.setSeq(pacientePesquisado);
		grupoFamiliarPacienteNovo.setAgfSeq(agfSeq);
		aipGrupoFamiliarPacientesDAO.persistir(grupoFamiliarPacienteNovo);
		
		AipGrupoFamiliarPacientesJn jn = BaseJournalFactory.getBaseJournal(
				DominioOperacoesJournal.UPD, AipGrupoFamiliarPacientesJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		jn.setPacCodigo(pacientePesquisado);
		jn.setAgfSeq(agfSeq);
		aipGrupoFamiliarPacientesJnDAO.persistir(jn); 
		
	}
	/**
	 * RN03
	 * @param pacCodigo
	 * @throws ApplicationBusinessException
	 */
	public void vincularPacienteGrupoFamiliar(Integer pacCodigo , Integer agfSeq) throws ApplicationBusinessException{
		RapServidores servidorLogado = registroColaboradorFacade.obterServidorPorUsuario(obterLoginUsuarioLogado());
		AipGrupoFamiliarPacientes grupofamiliarPacientes = new AipGrupoFamiliarPacientes();
		grupofamiliarPacientes.setSeq(pacCodigo);
		grupofamiliarPacientes.setAgfSeq(agfSeq);
		aipGrupoFamiliarPacientesDAO.persistir(grupofamiliarPacientes);
		
		AipGrupoFamiliarPacientesJn jn = BaseJournalFactory.getBaseJournal(
				DominioOperacoesJournal.INS, AipGrupoFamiliarPacientesJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		jn.setPacCodigo(pacCodigo);
		jn.setAgfSeq(agfSeq);
		aipGrupoFamiliarPacientesJnDAO.persistir(jn); 
		
	}
	/**
	 * RN03
	 * @param pacCodigo
	 * @throws ApplicationBusinessException
	 */
	public void vincularAmbosPacienteGrupoFamiliar(Integer pacCodigoContexto,Integer pacCodigoSugerido, Integer prontuarioInformado) throws ApplicationBusinessException{
		RapServidores servidorLogado = registroColaboradorFacade.obterServidorPorUsuario(obterLoginUsuarioLogado());
		
		AipGrupoFamiliar grupofamiliar = new AipGrupoFamiliar();
		grupofamiliar.setServidor(servidorLogado);
		grupofamiliar.setCriadoEm(new Date());
		if(prontuarioInformado==null) {
			grupofamiliar.setSeq(grupoFamiliarDAO.obterValorSequencial());
		}
		else {
			AipGrupoFamiliar prontInformado = grupoFamiliarDAO.obterPorChavePrimaria(prontuarioInformado);
			if(prontInformado!=null) {
				throw new ApplicationBusinessException(AgrupamentoFamiliarONExceptionCode.MENSAGEM_PRONTUARIO_FAMILIA_EXISTENTE);	
			}
			grupofamiliar.setSeq(prontuarioInformado);
		}
		grupofamiliar = grupoFamiliarDAO.merge(grupofamiliar);
		
		AipGrupoFamiliarJn jn3 = BaseJournalFactory.getBaseJournal(
				DominioOperacoesJournal.INS, AipGrupoFamiliarJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		jn3.setSeq((grupofamiliar.getSeq()));
		jn3.setSerMatricula(servidorLogado.getId().getMatricula());
		jn3.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		jn3.setCriadoEm(new Date());
		aipGrupoFamiliarJnDAO.persistir(jn3);
		
		AipGrupoFamiliarPacientes grupoFamiliarPacienteContexto = new AipGrupoFamiliarPacientes();
		grupoFamiliarPacienteContexto.setSeq(pacCodigoContexto);
		grupoFamiliarPacienteContexto.setAgfSeq(grupofamiliar.getSeq());
		aipGrupoFamiliarPacientesDAO.persistir(grupoFamiliarPacienteContexto);
		
		AipGrupoFamiliarPacientesJn jn = BaseJournalFactory.getBaseJournal(
				DominioOperacoesJournal.INS, AipGrupoFamiliarPacientesJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		jn.setPacCodigo(pacCodigoContexto);
		jn.setAgfSeq(grupofamiliar.getSeq());
	
		aipGrupoFamiliarPacientesJnDAO.persistir(jn); 
		
		// #54277 - Permitir gerar prontuario família para um paciente sem dependentes
		if(!pacCodigoContexto.equals(pacCodigoSugerido)) {
			AipGrupoFamiliarPacientes grupoFamiliarSugerido = new AipGrupoFamiliarPacientes();
			grupoFamiliarSugerido.setSeq(pacCodigoSugerido);
			grupoFamiliarSugerido.setAgfSeq(grupofamiliar.getSeq());
			aipGrupoFamiliarPacientesDAO.persistir(grupoFamiliarSugerido);
			
			AipGrupoFamiliarPacientesJn jn2 = BaseJournalFactory.getBaseJournal(
					DominioOperacoesJournal.INS, AipGrupoFamiliarPacientesJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
			jn2.setPacCodigo(pacCodigoSugerido);
			jn2.setAgfSeq(grupofamiliar.getSeq());
			aipGrupoFamiliarPacientesJnDAO.persistir(jn2); 
		}
		
	}

	public AipGrupoFamiliarPacientesDAO getAipGrupoFamiliarPacientesDAO() {
		return aipGrupoFamiliarPacientesDAO;
	}


	public AipGrupoFamiliarJnDAO getAipGrupoFamiliarJnDAO() {
		return aipGrupoFamiliarJnDAO;
	}
	
	public AipGrupoFamiliarDAO getGrupoFamiliarDAO() {
		return grupoFamiliarDAO;
	}

	public void setGrupoFamiliarDAO(AipGrupoFamiliarDAO grupoFamiliarDAO) {
		this.grupoFamiliarDAO = grupoFamiliarDAO;
	}

	public AipPacientesDAO getPacienteDAO() {
		return pacienteDAO;
	}

	public void setPacienteDAO(AipPacientesDAO pacienteDAO) {
		this.pacienteDAO = pacienteDAO;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
}
