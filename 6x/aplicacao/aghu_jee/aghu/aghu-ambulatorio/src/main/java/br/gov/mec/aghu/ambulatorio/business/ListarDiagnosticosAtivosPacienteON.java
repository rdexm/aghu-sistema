package br.gov.mec.aghu.ambulatorio.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamDiagnosticoDAO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.DiagnosticosPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.DiagnosticosPacienteVO.Acao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ListarDiagnosticosAtivosPacienteON extends BaseBusiness {

	@EJB
	private ListarDiagnosticosAtivosPacienteRN listarDiagnosticosAtivosPacienteRN;
	
	private static final Log LOG = LogFactory.getLog(ListarDiagnosticosAtivosPacienteON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MamDiagnosticoDAO mamDiagnosticoDAO;
	
	@EJB
	private ICascaFacade cascaFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8169421772097290812L;

	public enum ListarDiagnosticosAtivosPacienteONExceptionCode implements
			BusinessExceptionCode {

		/**
		 * Parâmetro obrigatório
		 */
		MENSAGEM_DIAGNOSTICO_PARAMETRO_OBRIGATORIO,
		/**
		 * Falha ao inserir registro
		 */
		MENSAGEM_DIAGNOSTICO_FALHA_INSERIR,
		/**
		 * Falha ao excluir registro
		 */
		MENSAGEM_DIAGNOSTICO_FALHA_EXCLUIR,
		/**
		 * Falha ao atualizar registro
		 */
		MENSAGEM_DIAGNOSTICO_FALHA_ATUALIZAR, MENSAGEM_DIAGNOSTICO_FALHA_RESOLVER, MENSAGEM_DATA_FIM_PARAMETRO_OBRIGATORIO;
	}
	
	protected ListarDiagnosticosAtivosPacienteRN getListarDiagnosticosAtivosPacienteRN(){
		return listarDiagnosticosAtivosPacienteRN;
	}

	/**
	 * Inclui um novo registro de diagnóstico.
	 * 
	 * @param diagnostico
	 * @param servidor
	 */
	public void incluir(MamDiagnostico diagnostico, RapServidores servidor)
			throws ApplicationBusinessException {

		if ((diagnostico == null) || (servidor == null)) {
			throw new ApplicationBusinessException(
					ListarDiagnosticosAtivosPacienteONExceptionCode.MENSAGEM_DIAGNOSTICO_PARAMETRO_OBRIGATORIO);
		}

		// sempre insere com "ind_pendente" com valor "V".
		diagnostico.setIndPendente(DominioIndPendenteDiagnosticos.V);

		// insere os dados de auditoria
		diagnostico.setDthrCriacao(Calendar.getInstance().getTime());
		diagnostico.setDthrValida(Calendar.getInstance().getTime());
		diagnostico.setServidor(servidor);
		diagnostico.setServidorValida(servidor);

		if (diagnostico.getDataFim() != null) {
			diagnostico.setDthrMvto(new Date());
			diagnostico.setDthrValidaMvto(new Date());
			diagnostico.setServidorMovimento(servidor);
			diagnostico.setServidorValidaMovimento(servidor);
			diagnostico.setIndSituacao(DominioSituacao.I);
		} else {
			diagnostico.setIndSituacao(DominioSituacao.A);
		}

		// encontra a categoria do profissional.
		try {
			List<CseCategoriaProfissional> lista = getCascaFacade()
					.pesquisarCategoriaProfissional(servidor);
			CseCategoriaProfissional categoria = null;
			if (lista != null && lista.size() > 0) {
				categoria = lista.get(0);
			}
			getListarDiagnosticosAtivosPacienteRN().insert(diagnostico, categoria);
			this.getMamDiagnosticoDAO().persistir(diagnostico);
			this.getMamDiagnosticoDAO().flush();
		} catch (BaseRuntimeException e) {
			throw new ApplicationBusinessException(
					ListarDiagnosticosAtivosPacienteONExceptionCode.MENSAGEM_DIAGNOSTICO_FALHA_INSERIR);
		}
	}

	/**
	 * Regras para atualizar registro
	 * 
	 * @param diagnostico
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void atualizar(MamDiagnostico diagnostico)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (diagnostico == null) {
			throw new ApplicationBusinessException(
					ListarDiagnosticosAtivosPacienteONExceptionCode.MENSAGEM_DIAGNOSTICO_PARAMETRO_OBRIGATORIO);
		}

		boolean alterouDataInicio = this.isAlterouDataInicio(diagnostico);
		boolean alterouDataFim = this.isAlterouDataFim(diagnostico);
		boolean alterouCid = this.isAlterouCID(diagnostico);
		boolean alterouComplemento = this.isAlterouComplemento(diagnostico);
		try {
			this.getListarDiagnosticosAtivosPacienteRN().update(diagnostico,
					servidorLogado, alterouDataInicio,
					alterouDataFim, alterouCid);
		} catch (BaseRuntimeException e) {
			throw new ApplicationBusinessException(
					ListarDiagnosticosAtivosPacienteONExceptionCode.MENSAGEM_DIAGNOSTICO_FALHA_ATUALIZAR);
		}

		MamDiagnostico old = this.getMamDiagnosticoDAO().obterOld(diagnostico);
		Date agora = new Date();
		old.setDthrMvto(agora);
		old.setDthrValidaMvto(agora);
		old.setServidorMovimento(servidorLogado);
		old.setServidorValidaMovimento(servidorLogado);

		// se alterou e não alterou nem início nem complemento, inativa
		// diagnóstico e guarda
		// na auditoria.
		if (alterouDataFim && !alterouDataInicio && !alterouComplemento) {
			old.setDataFim(new Date(diagnostico.getDataFim().getTime()));
			old.setIndSituacao(DominioSituacao.I);
			// atualiza registro original com os dados de auditoria
			try {
				this.getMamDiagnosticoDAO().atualizar(old);
				this.getMamDiagnosticoDAO().flush();
			} catch (BaseRuntimeException e) {
				logError(e.getMessage(), e);
			}
		} else if (alterouDataFim && (alterouDataInicio || alterouComplemento)) {
			old.setIndSituacao(DominioSituacao.I);
			// atualiza registro original com os dados de auditoria
			try {
				this.getMamDiagnosticoDAO().atualizar(old);
				this.getMamDiagnosticoDAO().flush();
			} catch (BaseRuntimeException e) {
				logError(e.getMessage(), e);
			}

			// insere outro registro com as novas informações, tb inativo
			// e com dados de auditoria.
			MamDiagnostico novo = new MamDiagnostico();
			novo.setDthrMvto(agora);
			novo.setDthrValidaMvto(agora);
			novo.setServidorMovimento(servidorLogado);
			novo.setServidorValidaMovimento(servidorLogado);
			novo.setIndSituacao(DominioSituacao.I);
			novo.setDthrCriacao(agora);
			novo.setDthrValida(agora);
			novo.setServidor(servidorLogado);
			novo.setServidorValida(servidorLogado);
			novo.setDiagnosticoFilho(old);
			novo.setPaciente(old.getPaciente());
			novo.setAtendimento(old.getAtendimento());
			novo.setIndPendente(old.getIndPendente());
			// atribui os valores vindos da tela
			novo.setData(new Date(diagnostico.getData().getTime()));
			novo.setDataFim(new Date(diagnostico.getDataFim().getTime()));
			novo.setComplemento(diagnostico.getComplemento());
			novo.setCid(diagnostico.getCid());

			// encontra a categoria do profissional.
			try {
				List<CseCategoriaProfissional> lista = getCascaFacade()
						.pesquisarCategoriaProfissional(
								servidorLogado);
				CseCategoriaProfissional categoria = null;
				if (lista != null && lista.size() > 0) {
					categoria = lista.get(0);
				}
				getListarDiagnosticosAtivosPacienteRN().insert(novo, categoria);
				this.getMamDiagnosticoDAO().persistir(novo);
				this.getMamDiagnosticoDAO().flush();
			} catch (BaseRuntimeException e) {
				throw new ApplicationBusinessException(
						ListarDiagnosticosAtivosPacienteONExceptionCode.MENSAGEM_DIAGNOSTICO_FALHA_INSERIR);
			}
		} else if (alterouDataInicio || alterouComplemento) {
			// insere um registro com os valores originais mais os dados de
			// auditoria e situação == 'I'

			old.setIndSituacao(DominioSituacao.I);

			// atualiza registro original com os dados de auditoria
			try {
				this.getMamDiagnosticoDAO().atualizar(old);
				this.getMamDiagnosticoDAO().flush();
			} catch (BaseRuntimeException e) {
				logError(e.getMessage(), e);
			}

			// insere registro novo também com dados de auditoria e inativo.

			diagnostico.setIndSituacao(DominioSituacao.A);
			diagnostico.setDiagnosticoFilho(old);
			diagnostico.setSeq(null);

			// insere registro original com os dados de auditoria
			this.incluir(diagnostico, servidorLogado);

		}

	}

	/**
	 * Regras para exclusão de registro.
	 * 
	 * @param diagnostico
	 * @throws ApplicationBusinessException
	 */
	public void excluir(MamDiagnostico diagnostico) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (diagnostico == null) {
			throw new ApplicationBusinessException(
					ListarDiagnosticosAtivosPacienteONExceptionCode.MENSAGEM_DIAGNOSTICO_PARAMETRO_OBRIGATORIO);
		}

		try {
			MamDiagnostico old = this.getMamDiagnosticoDAO().obterOld(
					diagnostico);
			old.setDataFim(null);
			old.setDthrMvto(Calendar.getInstance().getTime());
			old.setDthrValidaMvto(Calendar.getInstance().getTime());
			old.setServidorMovimento(servidorLogado);
			old.setServidorValidaMovimento(servidorLogado);
			old.setIndSituacao(DominioSituacao.I);
			old.setIndPendente(DominioIndPendenteDiagnosticos.C);
			this.getListarDiagnosticosAtivosPacienteRN().delete(old);

			this.getMamDiagnosticoDAO().atualizar(old);
			this.getMamDiagnosticoDAO().flush();
		} catch (BaseRuntimeException e) {
			throw new ApplicationBusinessException(
					ListarDiagnosticosAtivosPacienteONExceptionCode.MENSAGEM_DIAGNOSTICO_FALHA_EXCLUIR);
		}
	}

	/**
	 * Exclui todos os diagnósticos de um atendimento, executndo as devidas
	 * triggers.
	 * 
	 * @param atendimento
	 * @throws ApplicationBusinessException
	 */
	public void excluirDiagnosticosPorCidAtendimentos(
			AghAtendimentos atendimento) throws ApplicationBusinessException {
		MamDiagnosticoDAO daoDiagnostico = getMamDiagnosticoDAO();

		List<MamDiagnostico> diagnosticosDoAtendimento = daoDiagnostico
				.listarDiagnosticosPorAtendimento(atendimento);

		for (MamDiagnostico diagnostico : diagnosticosDoAtendimento) {
			this.excluir(diagnostico);
		}
	}

	public void resolver(MamDiagnostico diagnostico)
			throws ApplicationBusinessException {

		if (diagnostico == null) {
			throw new ApplicationBusinessException(
					ListarDiagnosticosAtivosPacienteONExceptionCode.MENSAGEM_DIAGNOSTICO_PARAMETRO_OBRIGATORIO);
		}
		if (diagnostico.getDataFim() == null) {
			throw new ApplicationBusinessException(
					ListarDiagnosticosAtivosPacienteONExceptionCode.MENSAGEM_DATA_FIM_PARAMETRO_OBRIGATORIO);
		}

		try {
			this.atualizar(diagnostico);
		} catch (BaseRuntimeException e) {
			throw new ApplicationBusinessException(
					ListarDiagnosticosAtivosPacienteONExceptionCode.MENSAGEM_DIAGNOSTICO_FALHA_RESOLVER);
		}
	}

	/**
	 * Verifica se houve alteração na data de início.
	 * 
	 * @param diagnostico
	 * @return
	 */
	public boolean isAlterouDataInicio(MamDiagnostico diagnostico) {

		// se ja tem seq, ja foi salvo no banco
		if (diagnostico != null && diagnostico.getSeq() != null) {
			// busca o antigo q esta no banco
			MamDiagnostico old = this.getMamDiagnosticoDAO().obterOld(
					diagnostico);

			// se encontrou antigo
			if (old != null && old.getSeq() != null) {
				// e a data de criação mudou=ALTERAÇÃO PENDENTE
				if (!diagnostico.getData().equals(old.getData())) {
					return true;
				}
			}
			// se não tem nada no banco e a data de criação foi preenchida =
			// ALTERAÇÃO PENDENTE
		} else if (diagnostico.getData() != null) {
			return true;
		}
		return false;
	}

	/**
	 * Verifica se houve alteração na data de fim.
	 * 
	 * @param diagnostico
	 * @return
	 */
	public boolean isAlterouDataFim(MamDiagnostico diagnostico) {

		// se ja tem seq, ja foi salvo no banco
		if (diagnostico != null && diagnostico.getSeq() != null) {
			// busca o antigo q esta no banco
			MamDiagnostico old = this.getMamDiagnosticoDAO().obterOld(
					diagnostico);

			// se encontrou antigo
			if (old != null && old.getSeq() != null) {
				// e a data de criação mudou=ALTERAÇÃO PENDENTE
				if ((diagnostico.getDataFim() != null)
						&& (!diagnostico.getDataFim().equals(old.getDataFim()))) {
					return true;
				}
			}
			// se não tem nada no banco e a data de criação foi preenchida =
			// ALTERAÇÃO PENDENTE
		} else if (diagnostico.getDataFim() != null) {
			return true;
		}
		return false;
	}

	/**
	 * Verifica se houve alteração no CID.
	 * 
	 * @param diagnostico
	 * @return
	 */
	public boolean isAlterouCID(MamDiagnostico diagnostico) {

		// se ja tem seq, ja foi salvo no banco
		if (diagnostico != null && diagnostico.getSeq() != null) {
			// busca o antigo q esta no banco
			MamDiagnostico old = this.getMamDiagnosticoDAO().obterOld(
					diagnostico);

			// se encontrou antigo
			if (old != null && old.getSeq() != null) {
				// e a data de criação mudou=ALTERAÇÃO PENDENTE
				if (!diagnostico.getCid().equals(old.getCid())) {
					return true;
				}
			}
			// se não tem nada no banco e a data de criação foi preenchida =
			// ALTERAÇÃO PENDENTE
		} else if (diagnostico.getCid() != null) {
			return true;
		}
		return false;
	}

	/**
	 * Verifica se houve alteração no complemento.
	 * 
	 * @param diagnostico
	 * @return
	 */
	public boolean isAlterouComplemento(MamDiagnostico diagnostico) {

		// se ja tem seq, ja foi salvo no banco
		if (diagnostico != null && diagnostico.getSeq() != null) {
			// busca o antigo q esta no banco
			MamDiagnostico old = this.getMamDiagnosticoDAO().obterOld(
					diagnostico);

			// se encontrou antigo
			if (old != null && old.getSeq() != null) {
				// e a data de criação mudou=ALTERAÇÃO PENDENTE
				if (!StringUtils.equalsIgnoreCase(diagnostico.getComplemento(),
						old.getComplemento())) {
					return true;
				}
			}
			// se não tem nada no banco e a data de criação foi preenchida =
			// ALTERAÇÃO PENDENTE
		} else if (diagnostico.getComplemento() != null) {
			return true;
		}
		return false;
	}

	public void salvarListaDiganosticosPaciente(
			List<DiagnosticosPacienteVO> listaVO) throws ApplicationBusinessException {

		for (DiagnosticosPacienteVO vo : listaVO) {
			Acao acao = vo.getAcao();
			switch (acao) {
			case ADICIONAR:
				this.incluir(vo.getMamDiagnostico(), vo.getServidor());
				break;
			case EXCLUIR:
				this.excluir(vo.getMamDiagnostico());
				break;
			case ALTERAR:
				this.atualizar(vo.getMamDiagnostico());
				break;
			case RESOLVER:
				this.resolver(vo.getMamDiagnostico());
				break;
			case NENHUM:
			default:
				break;
			}
		}

	}

	/**
	 * Retorna instância para MamDiagnosticoDAO
	 * 
	 * @return
	 */
	protected MamDiagnosticoDAO getMamDiagnosticoDAO() {
		return mamDiagnosticoDAO;
	}

	protected ICascaFacade getCascaFacade() {
		return this.cascaFacade;
	}

	public List<MamDiagnostico> buscarDiagnosticosAtivosPaciente(Integer codigo)
			throws ApplicationBusinessException {
		if (codigo == null) {
			throw new ApplicationBusinessException(
					ListarDiagnosticosAtivosPacienteONExceptionCode.MENSAGEM_DIAGNOSTICO_PARAMETRO_OBRIGATORIO);
		}
		return getMamDiagnosticoDAO().pesquisarAtivosPorPaciente(codigo);
	}

	public List<MamDiagnostico> buscarDiagnosticosResolvidosPaciente(
			Integer codigo) throws ApplicationBusinessException {
		if (codigo == null) {
			throw new ApplicationBusinessException(
					ListarDiagnosticosAtivosPacienteONExceptionCode.MENSAGEM_DIAGNOSTICO_PARAMETRO_OBRIGATORIO);
		}
		return getMamDiagnosticoDAO().pesquisarResolvidosPorPaciente(codigo);
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
