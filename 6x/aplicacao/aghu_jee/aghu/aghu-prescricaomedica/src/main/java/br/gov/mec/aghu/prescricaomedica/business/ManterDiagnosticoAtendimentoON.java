package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidAtendimentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterDiagnosticoAtendimentoON extends BaseBusiness {


@EJB
private ManterDiagnosticoAtendimentoRN manterDiagnosticoAtendimentoRN;

private static final Log LOG = LogFactory.getLog(ManterDiagnosticoAtendimentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmCidAtendimentoDAO mpmCidAtendimentoDAO;

@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3911427519429123223L;

	public enum ManterDiagnosticoAtendimentoONExceptionCode implements
			BusinessExceptionCode {

		/**
		 * Não permite inserir diagnóstico nulo
		 */
		MENSAGEM_ATENDIMENTO_ERRO_PARAMETRO_OBRIGATORIO,
		/**
		 * Falha ao incluir um registro de diagnóstico
		 */
		MENSAGEM_ATENDIMENTO_ERRO_INCLUIR,
		/**
		 * Falha ao excluir um registro de diagnóstico
		 */
		MENSAGEM_ATENDIMENTO_ERRO_EXCLUIR,
		/**
		 * Falha ao alterar registro de diagnóstico
		 */
		MENSAGEM_ATENDIMENTO_ERRO_ALTERAR,
		/**
		 * Servidor logado é nulo
		 */
		MENSAGEM_SERVIDOR_INVALIDO, MENSAGEM_CID_OBRIGATORIO, MENSAGEM_NUMERO_MINIMO_CARACTERES_CID;
	}

	//private ManterDiagnosticoAtendimentoRN manterDiagnosticoAtendimentoRN = manterDiagnosticoAtendimentoRN;
	
	
	protected ManterDiagnosticoAtendimentoRN getManterDiagnosticoAtendimentoRN(){
		return manterDiagnosticoAtendimentoRN;
	}

	/**
	 * Inclui um novo diagnostico de atendimento para um paciente.
	 * 
	 * 
	 * @param atendimento
	 *            Código de atendimento
	 * @param servidor
	 * @throws ApplicationBusinessException
	 */
	public void incluir(MpmCidAtendimento atendimento, RapServidores servidor)
			throws ApplicationBusinessException {

		if (atendimento == null) {
			throw new ApplicationBusinessException(
					ManterDiagnosticoAtendimentoONExceptionCode.MENSAGEM_ATENDIMENTO_ERRO_PARAMETRO_OBRIGATORIO);
		}
		if (servidor == null) {
			throw new ApplicationBusinessException(
					ManterDiagnosticoAtendimentoONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}
		if (atendimento.getCid() == null) {
			throw new ApplicationBusinessException(
					ManterDiagnosticoAtendimentoONExceptionCode.MENSAGEM_CID_OBRIGATORIO);
		}
		try {
			AipPacientes paciente = null;
			paciente = atendimento.getAtendimento().getPaciente();

			// passa pelas 5 regras de negócio antes da inserção.
			String complemento = atendimento.getComplemento();
			if (complemento != null) {
				complemento = complemento.trim();
			}
			atendimento.setComplemento(complemento);
			getManterDiagnosticoAtendimentoRN().insert(atendimento, paciente,
					servidor);

			atendimento.setPrioridadeInicio(DominioPrioridadeCid.N);
			atendimento.setPrincipalAlta(false);
			this.getMpmCidAtendimentoDAO().persistir(atendimento);
			this.getMpmCidAtendimentoDAO().flush();
		} catch (BaseRuntimeException e) {
			throw new ApplicationBusinessException(
					ManterDiagnosticoAtendimentoONExceptionCode.MENSAGEM_ATENDIMENTO_ERRO_INCLUIR);
		}
	}

	/**
	 * Exclui um registro. Pela regra, o sistema não exclui o registro mas
	 * apenas atualiza a data de fim com a data atual do sistema.
	 * 
	 * @param atendimento
	 *            Código de atendimento vigente.
	 * @throws ApplicationBusinessException
	 */
	public void excluir(MpmCidAtendimento atendimento, RapServidores servidor)
			throws ApplicationBusinessException {

		if (atendimento == null) {
			throw new ApplicationBusinessException(
					ManterDiagnosticoAtendimentoONExceptionCode.MENSAGEM_ATENDIMENTO_ERRO_PARAMETRO_OBRIGATORIO);
		}
		if (servidor == null) {
			throw new ApplicationBusinessException(
					ManterDiagnosticoAtendimentoONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}

		try {
			// ao invés de excluir, apenas atualiza o registro no banco
			// colocando a data de fim de atendimento como a data atual do
			// sistema.

			getManterDiagnosticoAtendimentoRN().delete(atendimento, servidor);

			this.getMpmCidAtendimentoDAO().merge(atendimento);
			this.getMpmCidAtendimentoDAO().flush();
		} catch (BaseRuntimeException e) {
			throw new ApplicationBusinessException(
					ManterDiagnosticoAtendimentoONExceptionCode.MENSAGEM_ATENDIMENTO_ERRO_EXCLUIR);
		}
	}

	/**
	 * Altera um diagnóstico de atendimento. Quando for atualização normal, não
	 * utiliza o método "update" da RN. Este método é apenas para exclusão.
	 * 
	 * @param atendimento
	 *            Código do atendimento.
	 * @param servidor
	 * @throws ApplicationBusinessException
	 */
	public void alterar(MpmCidAtendimento atendimento, RapServidores servidor)
			throws ApplicationBusinessException {

		if (atendimento == null) {
			throw new ApplicationBusinessException(
					ManterDiagnosticoAtendimentoONExceptionCode.MENSAGEM_ATENDIMENTO_ERRO_PARAMETRO_OBRIGATORIO);
		}
		if (servidor == null) {
			throw new ApplicationBusinessException(
					ManterDiagnosticoAtendimentoONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}

		try {

			getManterDiagnosticoAtendimentoRN().update(atendimento, servidor);
			
//			atendimento.setComplemento(complementoPendente);
			this.getMpmCidAtendimentoDAO().merge(atendimento);
			this.getMpmCidAtendimentoDAO().flush();
		} catch (BaseRuntimeException e) {
			throw new ApplicationBusinessException(
					ManterDiagnosticoAtendimentoONExceptionCode.MENSAGEM_ATENDIMENTO_ERRO_ALTERAR);
		}
	}

	/**
	 * Obtém uma listagem de MpmCidAtendimento.
	 * 
	 * @param atendimento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MpmCidAtendimento> buscarMpmCidsPorAtendimento(
			AghAtendimentos atendimento) throws ApplicationBusinessException {
		if (atendimento == null) {
			throw new ApplicationBusinessException(
					ManterDiagnosticoAtendimentoONExceptionCode.MENSAGEM_ATENDIMENTO_ERRO_PARAMETRO_OBRIGATORIO);
		}
		return getMpmCidAtendimentoDAO().listar(atendimento);
	}

	/**
	 * Retorna instância para MpmCidAtendimentoDAO.
	 * 
	 * @return Instância de MpmCidAtendimentoDAO
	 */
	protected MpmCidAtendimentoDAO getMpmCidAtendimentoDAO() {
		return mpmCidAtendimentoDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
}
