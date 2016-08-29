package br.gov.mec.aghu.emergencia.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.dao.MamEmgAgrupaEspDAO;
import br.gov.mec.aghu.emergencia.dao.MamEmgAgrupaEspJnDAO;
import br.gov.mec.aghu.emergencia.dao.MamEmgEspecialidadeJnDAO;
import br.gov.mec.aghu.emergencia.dao.MamEmgEspecialidadesDAO;
import br.gov.mec.aghu.emergencia.dao.MamEmgGrpPlanoXPerfilDAO;
import br.gov.mec.aghu.emergencia.dao.MamEmgServEspecialidadeDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.emergencia.vo.AgrupamentoEspecialidadeEmergenciaVO;
import br.gov.mec.aghu.emergencia.vo.EspecialidadeEmergenciaUnidadeVO;
import br.gov.mec.aghu.emergencia.vo.EspecialidadeEmergenciaVO;
import br.gov.mec.aghu.model.MamEmgAgrupaEsp;
import br.gov.mec.aghu.model.MamEmgAgrupaEspJn;
import br.gov.mec.aghu.model.MamEmgEspecialidadeJn;
import br.gov.mec.aghu.model.MamEmgEspecialidades;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.service.ServiceException;

/**
 * Regras de negócio relacionadas à entidade MamEmgEspecialidades
 * 
 * @author luismoura
 * 
 */
@Stateless
public class MamEmgEspecialidadesRN extends BaseBusiness {
	private static final long serialVersionUID = -4599511732141125111L;

	public enum MamEmgEspecialidadesRNExceptionCode implements BusinessExceptionCode {
		ERRO_DELETE_MSG_MAM_EMG_AGRUPA_ESPS, //
		ERRO_DELETE_MSG_MAM_EMG_GRP_PLANO_X_PERFIS, //
		ERRO_DELETE_MSG_MAM_EMG_SERV_ESPECIALIDADES, //
		ERRO_INSERCAO_REGISTRO_EXISTENTE, //
		ERRO_ASSOCIAR_ESPECIALIDADE_EXISTENTE, //
		MSG_INSERCAO_ESPECIALIDADE_SUCESSO, //
		MSG_ALTERACAO_ESPECIALIDADE_SUCESSO, //
		MSG_ASSOCIACAO_ESPECIALIDADE_SUCESSO, //
		MSG_ALTERACAO_SITUACAO_ESPECIALIDADE_SUCESSO, //
		MSG_DELETE_ESPECIALIDADE_SUCESSO, //
		MSG_DELETE_ASSOCIACAO_SUCESSO, //
		MENSAGEM_SERVICO_INDISPONIVEL, //
		;
	}

	@Inject
	private IConfiguracaoService configuracaoService;

	@Inject
	private MamEmgEspecialidadesDAO mamEmgEspecialidadesDAO;

	@Inject
	private MamEmgEspecialidadeJnDAO mamEmgEspecialidadeJnDAO;

	@Inject
	private MamEmgAgrupaEspDAO mamEmgAgrupaEspDAO;

	@Inject
	private MamEmgAgrupaEspJnDAO mamEmgAgrupaEspJnDAO;

	@Inject
	private MamEmgGrpPlanoXPerfilDAO mamEmgGrpPlanoXPerfilDAO;

	@Inject
	private MamEmgServEspecialidadeDAO mamEmgServEspecialidadeDAO;
	
	@Inject
	private RapServidoresDAO servidoresDAO;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;

	@Override
	protected Log getLogger() {
		return null;
	}

	/**
	 * Retorna as especialidades ativas de acordo com o param especificado.
	 * 
	 * @param param
	 * @return
	 * @throws ServiceException
	 */
	public List<Especialidade> pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(String param, Integer maxResults) throws ApplicationBusinessException {
		try {
			return configuracaoService.pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(param, maxResults);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(MamEmgEspecialidadesRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}

	/**
	 * Retorna o número de especialidades ativas de acordo com o param especificado.
	 * 
	 * @param param
	 * @return
	 * @throws ServiceException
	 */
	public Long pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaCount(String param) throws ApplicationBusinessException {
		try {
			return configuracaoService.pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaCount(param);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(MamEmgEspecialidadesRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}

	public List<EspecialidadeEmergenciaVO> pesquisarEspecialidadeEmergenciaVO(Short seq, DominioSituacao indSituacao) throws ApplicationBusinessException {

		List<EspecialidadeEmergenciaVO> result = new ArrayList<EspecialidadeEmergenciaVO>();

		// Busca as especialidades da emergencia
		List<MamEmgEspecialidades> mamEmgEspecialidades = mamEmgEspecialidadesDAO.pesquisarEspecialidadesEmergencia(seq, indSituacao);

		// Busca as especialidades via serviço, passando os seqs como parametros
		if (!mamEmgEspecialidades.isEmpty()) {

			// Cria um map com os seqs
			Map<Short, MamEmgEspecialidades> mapEspecialidades = new HashMap<Short, MamEmgEspecialidades>();
			for (MamEmgEspecialidades espEmg : mamEmgEspecialidades) {
				mapEspecialidades.put(espEmg.getSeq(), espEmg);
			}

			List<Especialidade> especialidades = new ArrayList<Especialidade>();
			try {
				List<Short> seqs = new ArrayList<Short>(mapEspecialidades.keySet());
				especialidades = configuracaoService.pesquisarEspecialidadesAtivasPorSeqs(seqs);
			} catch (ServiceException e) {
				throw new ApplicationBusinessException(MamEmgEspecialidadesRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
			}

			for (Especialidade esp : especialidades) {
				EspecialidadeEmergenciaVO especialidadeEmergenciaVO = new EspecialidadeEmergenciaVO();
				especialidadeEmergenciaVO.setEspecialidade(esp);
				especialidadeEmergenciaVO.setEspecialidadeEmergencia(mapEspecialidades.get(esp.getSeq()));
				result.add(especialidadeEmergenciaVO);
			}
		}

		return result;
	}

	public List<AgrupamentoEspecialidadeEmergenciaVO> pesquisarAgrupamentoEspecialidadeEmergenciaVO(Short seq) throws ApplicationBusinessException {

		List<AgrupamentoEspecialidadeEmergenciaVO> result = new ArrayList<AgrupamentoEspecialidadeEmergenciaVO>();

		// Busca as especialidades da emergencia
		List<MamEmgAgrupaEsp> mamEmgAgrupaEsp = mamEmgAgrupaEspDAO.pesquisarAgrupamentoEspecialidadeEmergencia(seq);

		// Busca as especialidades via serviço, passando os seqs como parametros
		if (!mamEmgAgrupaEsp.isEmpty()) {

			// Cria um map com os seqs
			Map<Short, MamEmgAgrupaEsp> mapAgrupamentos = new HashMap<Short, MamEmgAgrupaEsp>();
			for (MamEmgAgrupaEsp espAgrEmg : mamEmgAgrupaEsp) {
				mapAgrupamentos.put(espAgrEmg.getId().getEspSeq(), espAgrEmg);
			}

			List<Especialidade> especialidades = new ArrayList<Especialidade>();
			try {
				List<Short> seqs = new ArrayList<Short>(mapAgrupamentos.keySet());
				especialidades = configuracaoService.pesquisarEspecialidadesAtivasPorSeqs(seqs);
			} catch (ServiceException e) {
				throw new ApplicationBusinessException(MamEmgEspecialidadesRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
			}
			for (Especialidade esp : especialidades) {
				AgrupamentoEspecialidadeEmergenciaVO agrupamentoEspecialidadeEmergenciaVO = new AgrupamentoEspecialidadeEmergenciaVO();
				agrupamentoEspecialidadeEmergenciaVO.setEspecialidade(esp);
				agrupamentoEspecialidadeEmergenciaVO.setAgrupamentoEspecialidadeEmergencia(mapAgrupamentos.get(esp.getSeq()));
				result.add(agrupamentoEspecialidadeEmergenciaVO);
			}
		}

		return result;
	}

	/**
	 * Exclui o registro executando as regras de pre-delete
	 * 
	 * RN01 de #12173 - Manter cadastro de especialidades
	 * 
	 * @param espSeq
	 * @throws ApplicationBusinessException
	 */
	public void excluir(Short espSeq) throws ApplicationBusinessException {

		MamEmgEspecialidades especialidadeEmergencia = new MamEmgEspecialidades(); 
		
		MamEmgEspecialidades especialidadeEmergenciaExistentes = mamEmgEspecialidadesDAO.obterPorChavePrimaria(espSeq);
		
		if(especialidadeEmergenciaExistentes != null){
			
			especialidadeEmergencia = especialidadeEmergenciaExistentes;
		}

		if (especialidadeEmergencia != null) {
			Boolean existsEmgAgrupaEsp = mamEmgAgrupaEspDAO.pesquisarAgrupaEspEmergenciaExists(espSeq);
			if (existsEmgAgrupaEsp) {
				throw new ApplicationBusinessException(MamEmgEspecialidadesRNExceptionCode.ERRO_DELETE_MSG_MAM_EMG_AGRUPA_ESPS);
			}

			Boolean existsEmgGrpPlanoXPerfil = mamEmgGrpPlanoXPerfilDAO.pesquisarGrupoPlanoPerfilEmergenciaExists(espSeq);
			if (existsEmgGrpPlanoXPerfil) {
				throw new ApplicationBusinessException(MamEmgEspecialidadesRNExceptionCode.ERRO_DELETE_MSG_MAM_EMG_GRP_PLANO_X_PERFIS);
			}

			Boolean existsEmgServEspecialidade = mamEmgServEspecialidadeDAO.pesquisarServEspecialidadeEmergenciaExists(espSeq);
			if (existsEmgServEspecialidade) {
				throw new ApplicationBusinessException(MamEmgEspecialidadesRNExceptionCode.ERRO_DELETE_MSG_MAM_EMG_SERV_ESPECIALIDADES);
			}

			// Realiza insert na journal de mam_emg_especialidades
			this.inserirJournalEspecialidadeEmergencia(especialidadeEmergencia, DominioOperacoesJournal.DEL);

			this.mamEmgEspecialidadesDAO.remover(especialidadeEmergencia);
		}
	}

	/**
	 * Insere um registro na journal de MamEmgEspecialidades
	 * 
	 * @param mamEspecialidadeEmergenciaOriginal
	 * @param operacao
	 */
	private void inserirJournalEspecialidadeEmergencia(MamEmgEspecialidades mamEspecialidadeEmergenciaOriginal, DominioOperacoesJournal operacao) {

		MamEmgEspecialidadeJn mamEspecialidadeEmergenciaJn = BaseJournalFactory.getBaseJournal(operacao, MamEmgEspecialidadeJn.class, usuario.getLogin());

		mamEspecialidadeEmergenciaJn.setCriadoEm(mamEspecialidadeEmergenciaOriginal.getCriadoEm());
		mamEspecialidadeEmergenciaJn.setEspSeq(mamEspecialidadeEmergenciaOriginal.getEspSeq());

		String indSituacao = mamEspecialidadeEmergenciaOriginal.getIndSituacao() != null ? mamEspecialidadeEmergenciaOriginal.getIndSituacao().toString()
				: null;
		mamEspecialidadeEmergenciaJn.setIndSituacao(indSituacao);

		mamEspecialidadeEmergenciaJn.setSerMatricula(mamEspecialidadeEmergenciaOriginal.getSerMatricula());
		mamEspecialidadeEmergenciaJn.setSerVinCodigo(mamEspecialidadeEmergenciaOriginal.getSerVinCodigo());

		mamEmgEspecialidadeJnDAO.persistir(mamEspecialidadeEmergenciaJn);

	}

	/**
	 * Insere um registro de MamEmgEspecialidades, executando trigger de pre-insert
	 * 
	 * RN02 de #12173 - Manter cadastro de especialidades
	 * 
	 * @param especialidadeEmergencia
	 * @throws ApplicationBusinessException
	 */
	private void inserir(MamEmgEspecialidades especialidadeEmergencia) throws ApplicationBusinessException {
		MamEmgEspecialidades especialidadeExistente = mamEmgEspecialidadesDAO.obterPorChavePrimaria(especialidadeEmergencia.getEspSeq());
		if (especialidadeExistente != null) {
			throw new ApplicationBusinessException(MamEmgEspecialidadesRNExceptionCode.ERRO_INSERCAO_REGISTRO_EXISTENTE);
		
		}else{
		//especialidadeEmergencia = new MamEmgEspecialidades();
			this.preInserir(especialidadeEmergencia);
			this.mamEmgEspecialidadesDAO.persistir(especialidadeEmergencia);
		}
	}
//try
	/**
	 * Atualiza um registro de MamEmgEspecialidades
	 * 
	 * RN02 de #12173 - Manter cadastro de especialidades
	 * 
	 * @param especialidadeEmergencia
	 */
	private void alterar(MamEmgEspecialidades especialidadeEmergencia, MamEmgEspecialidades especialidadeEmergenciaOriginal)
			throws ApplicationBusinessException {
		especialidadeEmergencia.setSerVinCodigo(usuario.getVinculo());
		especialidadeEmergencia.setSerMatricula(usuario.getMatricula());
		if (CoreUtil.modificados(especialidadeEmergencia.getIndSituacao(), especialidadeEmergenciaOriginal.getIndSituacao())) {
			this.inserirJournalEspecialidadeEmergencia(especialidadeEmergenciaOriginal, DominioOperacoesJournal.UPD);
		}
		mamEmgEspecialidadesDAO.atualizar(especialidadeEmergencia);
	}

	/**
	 * Insere ou atualiza um registro de MamEmgEspecialidades, executando trigger de pre-insert ou pre-update
	 * 
	 * RN02 de #12173 - Manter cadastro de especialidades
	 * 
	 * @param especialidadeEmergencia
	 * @param create
	 * @throws ApplicationBusinessException
	 */
	public void persistir(MamEmgEspecialidades especialidadeEmergencia, boolean create) throws ApplicationBusinessException {
		if (create) {
			this.inserir(especialidadeEmergencia);
		} else {
			MamEmgEspecialidades especialidadeEmergenciaOriginal = this.mamEmgEspecialidadesDAO.obterOriginal(especialidadeEmergencia);
			this.alterar(especialidadeEmergencia, especialidadeEmergenciaOriginal);
		}
	}

	/**
	 * Trigger de pre-insert de MamEmgEspecialidades
	 * 
	 * RN03 de #12173 - Manter cadastro de especialidades
	 * 
	 * @ORADB MAM_EMG_ESPECIALIDADES.MAMT_EEP_BRI
	 * @param especialidadeEmergencia
	 * @throws ApplicationBusinessException
	 */
	private void preInserir(MamEmgEspecialidades especialidadeEmergencia) throws ApplicationBusinessException {
		especialidadeEmergencia.setCriadoEm(new Date());
		especialidadeEmergencia.setSerVinCodigo(usuario.getVinculo());
		especialidadeEmergencia.setSerMatricula(usuario.getMatricula());
	}

	/**
	 * Insere um registro de MamEmgAgrupaEsp, executando trigger de pre-insert
	 * 
	 * RN04 de #12173 – Manter cadastro de especialidades
	 * 
	 * @param mamEmgAgrupaEsp
	 * @throws ApplicationBusinessException
	 */
	public void inserirMamEmgAgrupaEsp(MamEmgAgrupaEsp mamEmgAgrupaEsp) throws ApplicationBusinessException {
		MamEmgAgrupaEsp agrup = mamEmgAgrupaEspDAO.obterPorChavePrimaria(mamEmgAgrupaEsp.getId());
		if (agrup != null) {
			throw new ApplicationBusinessException(MamEmgEspecialidadesRNExceptionCode.ERRO_ASSOCIAR_ESPECIALIDADE_EXISTENTE);
		}
		this.preInserir(mamEmgAgrupaEsp);
		this.mamEmgAgrupaEspDAO.persistir(mamEmgAgrupaEsp);
	}

	/**
	 * Ativa/Inativa um registro de MamEmgAgrupaEsp, executando trigger de pre-update
	 * 
	 * RN04 de #12173 – Manter cadastro de especialidades
	 * 
	 * @param mamEmgAgrupaEsp
	 * @throws ApplicationBusinessException
	 */
	public void ativarInativarMamEmgAgrupaEsp(MamEmgAgrupaEsp mamEmgAgrupaEsp) throws ApplicationBusinessException {

		String sit = DominioSituacao.A.toString().equals(mamEmgAgrupaEsp.getIndSituacao()) ? DominioSituacao.I.toString() : DominioSituacao.A.toString();
		mamEmgAgrupaEsp.setIndSituacao(sit);

		
		mamEmgAgrupaEsp.setRapServidores(servidoresDAO.obter(new RapServidoresId(usuario.getMatricula(), usuario.getVinculo())));
		
		//servidoresDAO

		this.inserirJournalAgrupamentoEspecialidadeEmergencia(mamEmgAgrupaEsp, DominioOperacoesJournal.UPD);

		this.mamEmgAgrupaEspDAO.atualizar(mamEmgAgrupaEsp);
	}

	/**
	 * Trigger de pre-insert de MamEmgAgrupaEsp
	 * 
	 * RN05 de #12173 - Manter cadastro de especialidades
	 * 
	 * @ORADB MAM_EMG_AGRUPA_ESPS.MAMT_EAE_BRI
	 * @param mamEmgAgrupaEsp
	 * @throws ApplicationBusinessException
	 */
	private void preInserir(MamEmgAgrupaEsp mamEmgAgrupaEsp) throws ApplicationBusinessException {
		mamEmgAgrupaEsp.setCriadoEm(new Date());
		
		mamEmgAgrupaEsp.setRapServidores(servidoresDAO.obter(new RapServidoresId(usuario.getMatricula(), usuario.getVinculo())));
	}

	/**
	 * Insere um registro na journal de MamEmgAgrupaEsp
	 * 
	 * @param mamEmgAgrupaEspOriginal
	 * @param operacao
	 */
	private void inserirJournalAgrupamentoEspecialidadeEmergencia(MamEmgAgrupaEsp mamEmgAgrupaEspOriginal, DominioOperacoesJournal operacao) {
		MamEmgAgrupaEspJn mamEmgAgrupaEspJn = BaseJournalFactory.getBaseJournal(operacao, MamEmgAgrupaEspJn.class, usuario.getLogin());
		mamEmgAgrupaEspJn.setEepEspSeq(mamEmgAgrupaEspOriginal.getId().getEepEspSeq());
		mamEmgAgrupaEspJn.setEspSeq(mamEmgAgrupaEspOriginal.getId().getEspSeq());
		mamEmgAgrupaEspJn.setIndSituacao(mamEmgAgrupaEspOriginal.getIndSituacao());
		mamEmgAgrupaEspJn.setCriadoEm(mamEmgAgrupaEspOriginal.getCriadoEm());
		mamEmgAgrupaEspJn.setSerMatricula(mamEmgAgrupaEspOriginal.getRapServidores().getId().getMatricula());
		mamEmgAgrupaEspJn.setSerVinCodigo(mamEmgAgrupaEspOriginal.getRapServidores().getId().getVinCodigo());
		
		
		mamEmgAgrupaEspJnDAO.persistir(mamEmgAgrupaEspJn);
	}

	/**
	 * Exclui um registro de MamEmgAgrupaEsp, executando trigger de pre-delete
	 * 
	 * RN06 de #12173 - Manter cadastro de especialidades
	 * 
	 * @param mamEmgAgrupaEsp
	 * @throws ApplicationBusinessException
	 */
	public void excluirMamEmgAgrupaEsp(MamEmgAgrupaEsp mamEmgAgrupaEsp) throws ApplicationBusinessException {

		if (!this.mamEmgAgrupaEspDAO.contains(mamEmgAgrupaEsp)) {
			mamEmgAgrupaEsp = mamEmgAgrupaEspDAO.obterPorChavePrimaria(mamEmgAgrupaEsp.getId());
		}

		this.inserirJournalAgrupamentoEspecialidadeEmergencia(mamEmgAgrupaEsp, DominioOperacoesJournal.DEL);

		this.mamEmgAgrupaEspDAO.remover(mamEmgAgrupaEsp);
	}

	public List<EspecialidadeEmergenciaUnidadeVO> obterEspecialidadesEmergenciaAssociadasUnidades(Short unfSeq) throws ApplicationBusinessException {
		List<Short> listaSeqsEspecialidadeEmerg = new ArrayList<Short>();
		List<Especialidade> listaEspecialidades = new ArrayList<Especialidade>();
		List<EspecialidadeEmergenciaUnidadeVO> listaEspecialidadesVO = new ArrayList<EspecialidadeEmergenciaUnidadeVO>();
		if (unfSeq != null) {
			listaSeqsEspecialidadeEmerg = mamEmgEspecialidadesDAO.pesquisarEspecialidadesEmergenciaAssociadasUnidades(unfSeq);
		}
		if (listaSeqsEspecialidadeEmerg.size() != 0) {
			listaEspecialidades = obterEspecialidadeEmergencia(listaSeqsEspecialidadeEmerg);
			for (Especialidade especialidade : listaEspecialidades) {
				EspecialidadeEmergenciaUnidadeVO especialidadeVO = new EspecialidadeEmergenciaUnidadeVO(especialidade.getSeq(), especialidade.getSigla());
				especialidadeVO.setEspSigla(especialidade.getSigla());
				especialidadeVO.setEspSeq(especialidade.getSeq());

				listaEspecialidadesVO.add(especialidadeVO);
			}
		}
		return listaEspecialidadesVO;
	}

	/**
	 * 
	 * @param conNumero
	 * @param espSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private List<Especialidade> obterEspecialidadeEmergencia(List<Short> listaSeqsEspecialidadeEmerg) throws ApplicationBusinessException {
		List<Especialidade> result = null;
		try {
			result = configuracaoService.pesquisarEspecialidadesAtivasPorSeqs(listaSeqsEspecialidadeEmerg);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(MamEmgEspecialidadesRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
	}

}
