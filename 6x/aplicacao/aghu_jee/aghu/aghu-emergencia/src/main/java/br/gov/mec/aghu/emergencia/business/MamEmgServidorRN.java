package br.gov.mec.aghu.emergencia.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.MamEmgEspecialidadesRN.MamEmgEspecialidadesRNExceptionCode;
import br.gov.mec.aghu.emergencia.dao.MamEmgEspecialidadesDAO;
import br.gov.mec.aghu.emergencia.dao.MamEmgServEspCoopDAO;
import br.gov.mec.aghu.emergencia.dao.MamEmgServEspCoopJnDAO;
import br.gov.mec.aghu.emergencia.dao.MamEmgServEspecialidadeDAO;
import br.gov.mec.aghu.emergencia.dao.MamEmgServEspecialidadeJnDAO;
import br.gov.mec.aghu.emergencia.dao.MamEmgServidorDAO;
import br.gov.mec.aghu.emergencia.dao.MamEmgServidorJnDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.emergencia.vo.ServidorEmergenciaVO;
import br.gov.mec.aghu.emergencia.vo.ServidorEspecialidadeEmergenciaVO;
import br.gov.mec.aghu.emergencia.vo.ServidorIdVO;
import br.gov.mec.aghu.model.MamEmgEspecialidades;
import br.gov.mec.aghu.model.MamEmgServEspCoop;
import br.gov.mec.aghu.model.MamEmgServEspCoopJn;
import br.gov.mec.aghu.model.MamEmgServEspecialidade;
import br.gov.mec.aghu.model.MamEmgServEspecialidadeJn;
import br.gov.mec.aghu.model.MamEmgServidor;
import br.gov.mec.aghu.model.MamEmgServidorJn;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.service.IRegistroColaboradorService;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.service.ServiceException;

/**
 * Regras de negócio relacionadas à entidade MamEmgServidor
 * 
 * @author luismoura
 * 
 */
@Stateless
public class MamEmgServidorRN extends BaseBusiness {
	private static final long serialVersionUID = 8974843126609840801L;

	public enum MamEmgServidorRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SUCESSO_CADASTRO_SERVIDOR_EMERGENCIA, //
		MENSAGEM_SUCESSO_ALTERACAO_SERVIDOR_EMERGENCIA, //
		MENSAGEM_SUCESSO_CADASTRO_ESPECIALIDADE_SERVIDOR, //
		MENSAGEM_ALTERACAO_SITUACAO_ESPECIALIDADE_SERVIDOR, //
		MENSAGEM_ERRO_COOPERACAO_SERVIDOR_JA_CADASTRADA, //
		MENSAGEM_ERRO_ESPECIALIDADE_SERVIDOR_JA_CADASTRADA, //
		MENSAGEM_SUCESSO_EXCLUSAO_ESPECIALIDADE_SERVIDOR, //
		MENSAGEM_SUCESSO_EXCLUSAO_COOPERACAO_SERVIDOR, //
		MENSAGEM_SUCESSO_EXCLUSAO_SERVIDOR, //
		MENSAGEM_ERRO_ESPECIALIDADE_SERVIDOR_POSSUI_COOPERACAO, //
		MENSAGEM_ERRO_SERVIDOR_POSSUI_ESPECIALIDADES, //
		MENSAGEM_SERVIDOR_JA_CADASTRADO, //
		MENSAGEM_SERVICO_INDISPONIVEL, //
		OPTIMISTIC_LOCK,
		;
	}

	@Inject
	private IRegistroColaboradorService registroColaboradorService;

	@Inject
	private IConfiguracaoService configuracaoService;

	@Inject
	private MamEmgServidorDAO mamEmgServidorDAO;

	@Inject
	private MamEmgServEspecialidadeDAO mamEmgServEspecialidadeDAO;

	@Inject
	private MamEmgServidorJnDAO mamEmgServidorJnDAO;

	@Inject
	private MamEmgServEspecialidadeJnDAO mamEmgServEspecialidadeJnDAO;

	@Inject
	private MamEmgEspecialidadesDAO mamEmgEspecialidadesDAO;

	@Inject
	private MamEmgServEspCoopDAO mamEmgServEspCoopDAO;

	@Inject
	private MamEmgServEspCoopJnDAO mamEmgServEspCoopJnDAO;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private RapServidoresDAO servidorDAO;

	@Override
	protected Log getLogger() {
		return null;
	}

	public List<ServidorEmergenciaVO> pesquisarServidorEmergenciaVO(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Short serVinCodigo, Integer matricula, String indSituacao, String nome) throws ApplicationBusinessException {

		List<ServidorEmergenciaVO> result = new ArrayList<ServidorEmergenciaVO>();

		// Busca as servidores da emergencia
		List<MamEmgServidor> mamEmgServidores = mamEmgServidorDAO.pesquisarServidoresEmergencia(serVinCodigo, matricula, indSituacao);

		// Busca as servidores via serviço, passando os seqs como parametros
		if (!mamEmgServidores.isEmpty()) {

			// Cria um map com os seqs
			Map<ServidorIdVO, MamEmgServidor> mapServidores = new HashMap<ServidorIdVO, MamEmgServidor>();
			List<Short> listVinCodigo = new ArrayList<Short>();
			List<Integer> listMatricula = new ArrayList<Integer>();
			for (MamEmgServidor servEmg : mamEmgServidores) {
				//mapServidores.put(new ServidorIdVO(servEmg.getSerVinCodigoByMamEseSerFk1(), servEmg.getSerMatriculaByMamEseSerFk1()), servEmg);
				mapServidores.put(new ServidorIdVO(servEmg.getRapServidoresByMamEseSerFk1().getId().getVinCodigo(), 
						servEmg.getRapServidoresByMamEseSerFk1().getId().getMatricula()), servEmg);
				listVinCodigo.add(servEmg.getRapServidoresByMamEseSerFk1().getId().getVinCodigo());
				listMatricula.add(servEmg.getRapServidoresByMamEseSerFk1().getId().getMatricula());
			}

			List<Servidor> servidores = new ArrayList<Servidor>();
			try {
				servidores = registroColaboradorService.pesquisarMedicosEmergencia(listVinCodigo, listMatricula, nome, firstResult, maxResult, orderProperty,
						asc);
			} catch (ServiceException e) {
				throw new ApplicationBusinessException(MamEmgServidorRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
			}

			for (Servidor serv : servidores) {
				MamEmgServidor servidorEmergencia = mapServidores.get(new ServidorIdVO(serv.getVinculo(), serv.getMatricula()));
				if (servidorEmergencia != null) {
					ServidorEmergenciaVO servidorEmergenciaVO = new ServidorEmergenciaVO();
					servidorEmergenciaVO.setServidor(serv);
					servidorEmergenciaVO.setServidorEmergencia(servidorEmergencia);
					result.add(servidorEmergenciaVO);
				}
			}
		}

		return result;
	}

	public Long pesquisarServidorEmergenciaVOCount(Short serVinCodigo, Integer matricula, String indSituacao, String nome) throws ApplicationBusinessException {

		// Busca as servidores da emergencia
		List<MamEmgServidor> mamEmgServidores = mamEmgServidorDAO.pesquisarServidoresEmergencia(serVinCodigo, matricula, indSituacao);

		Long numServEmg = Long.valueOf(mamEmgServidores.size());
		if (numServEmg == 0) {
			return numServEmg;
		}

		// Cria um map com os seqs
		List<Short> listVinCodigo = new ArrayList<Short>();
		List<Integer> listMatricula = new ArrayList<Integer>();
		for (MamEmgServidor servEmg : mamEmgServidores) {
			listVinCodigo.add(servEmg.getRapServidoresByMamEseSerFk1().getId().getVinCodigo());
			listMatricula.add(servEmg.getRapServidoresByMamEseSerFk1().getId().getMatricula());
		}

		Long numServ = null;
		try {
			numServ = registroColaboradorService.pesquisarMedicosEmergenciaCount(listVinCodigo, listMatricula, nome);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(MamEmgServidorRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}

		return numServEmg > numServ ? numServ : numServEmg;
	}

	public List<Servidor> pesquisarServidoresAtivosPorNomeOuVinculoMatricula(String param) throws ApplicationBusinessException {
		String nome = null;
		Short vinCodigo = null;
		Integer matricula = null;

		if (StringUtils.isNotBlank(param)) {
			String[] inputs = param.trim().split(" ");
			if (inputs.length == 2 && CoreUtil.isNumeroShort(inputs[0]) && CoreUtil.isNumeroInteger(inputs[1])) {
				vinCodigo = Short.parseShort(inputs[0]);
				matricula = Integer.parseInt(inputs[1]);
			} else {
				nome = param.trim();
			}
		}

		try {
			return registroColaboradorService.pesquisarServidoresAtivos(vinCodigo, matricula, nome, 100);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(MamEmgEspecialidadesRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}

	public Long pesquisarServidoresAtivosPorNomeOuVinculoMatriculaCount(String param) throws ApplicationBusinessException {
		String nome = null;
		Short vinCodigo = null;
		Integer matricula = null;

		if (StringUtils.isNotBlank(param)) {
			String[] inputs = param.trim().split(" ");
			if (inputs.length == 2 && CoreUtil.isNumeroShort(inputs[0]) && CoreUtil.isNumeroInteger(inputs[1])) {
				vinCodigo = Short.parseShort(inputs[0]);
				matricula = Integer.parseInt(inputs[1]);
			} else {
				nome = param.trim();
			}
		}

		try {
			return registroColaboradorService.pesquisarServidoresAtivosCount(vinCodigo, matricula, nome);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(MamEmgEspecialidadesRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}

	public List<ServidorEspecialidadeEmergenciaVO> pesquisarServidorEspecialidadeEmergenciaVO(Integer seq) throws ApplicationBusinessException {

		List<ServidorEspecialidadeEmergenciaVO> result = new ArrayList<ServidorEspecialidadeEmergenciaVO>();

		// Busca as servidores da emergencia
		List<MamEmgServEspecialidade> mamEmgServEspecialidade = mamEmgServEspecialidadeDAO.pesquisarServEspecialidadeEmergencia(seq);

		// Busca as servidores via serviço, passando os seqs como parametros
		if (!mamEmgServEspecialidade.isEmpty()) {

			// Cria um map com os seqs
			Map<Short, MamEmgServEspecialidade> mapEspecialidades = new HashMap<Short, MamEmgServEspecialidade>();
			for (MamEmgServEspecialidade servEspEmg : mamEmgServEspecialidade) {
				mapEspecialidades.put(servEspEmg.getMamEmgEspecialidades().getSeq(), servEspEmg);
			}

			List<Especialidade> especialidades = new ArrayList<Especialidade>();
			try {
				List<Short> seqs = new ArrayList<Short>(mapEspecialidades.keySet());
				especialidades = configuracaoService.pesquisarEspecialidadesAtivasPorSeqs(seqs);
			} catch (ServiceException e) {
				throw new ApplicationBusinessException(MamEmgEspecialidadesRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
			}

			for (Especialidade esp : especialidades) {
				ServidorEspecialidadeEmergenciaVO servidorEspecialidadeEmergenciaVO = new ServidorEspecialidadeEmergenciaVO();
				servidorEspecialidadeEmergenciaVO.setEspecialidade(esp);
				servidorEspecialidadeEmergenciaVO.setMamEmgServEspecialidade(mapEspecialidades.get(esp.getSeq()));
				result.add(servidorEspecialidadeEmergenciaVO);
			}
		}

		return result;
	}

	/**
	 * Exclui o registro executando as regras de pre-delete
	 * 
	 * RN10 de #12174 - Manter cadastro de servidores
	 * 
	 * @param eseSeq
	 * @throws ApplicationBusinessException
	 */
	public void excluir(Integer eseSeq) throws ApplicationBusinessException {

		try {
			MamEmgServidor servidorEmergencia = mamEmgServidorDAO.obterPorChavePrimaria(eseSeq);

			if (servidorEmergencia != null) {
				Boolean existsEmgAgrupaEsp = mamEmgServEspecialidadeDAO.pesquisarServEspecialidadeEmergenciaExists(eseSeq);
				if (existsEmgAgrupaEsp) {
					throw new ApplicationBusinessException(MamEmgServidorRNExceptionCode.MENSAGEM_ERRO_SERVIDOR_POSSUI_ESPECIALIDADES);
				}

				// Realiza insert na journal de mam_emg_servidores
				this.inserirJournalServidorEmergencia(servidorEmergencia, DominioOperacoesJournal.DEL);

				this.mamEmgServidorDAO.remover(servidorEmergencia);
			}
		} catch (BaseOptimisticLockException e) {
			throw new ApplicationBusinessException(MamEmgServidorRNExceptionCode.OPTIMISTIC_LOCK);
		}
		
	}

	/**
	 * Insere um registro na journal de MamEmgServidor
	 * 
	 * @ORADB MAM_EMG_SERVIDORES.MAMT_ESE_ARU
	 * @param mamServidorEmergenciaOriginal
	 * @param operacao
	 */
	private void inserirJournalServidorEmergencia(MamEmgServidor mamServidorEmergenciaOriginal, DominioOperacoesJournal operacao) {

		MamEmgServidorJn mamEmgServidorJn = BaseJournalFactory.getBaseJournal(operacao, MamEmgServidorJn.class, usuario.getLogin());

		mamEmgServidorJn.setCriadoEm(mamServidorEmergenciaOriginal.getCriadoEm());
		mamEmgServidorJn.setSeq(mamServidorEmergenciaOriginal.getSeq());

		mamEmgServidorJn.setIndSituacao(mamServidorEmergenciaOriginal.getIndSituacao());		
		
		mamEmgServidorJn.setSerMatriculaAtua(mamServidorEmergenciaOriginal.getRapServidoresByMamEseSerFk1().getId().getMatricula());
		mamEmgServidorJn.setSerVinCodigoAtua(mamServidorEmergenciaOriginal.getRapServidoresByMamEseSerFk1().getId().getVinCodigo());

		mamEmgServidorJn.setSerMatricula(mamServidorEmergenciaOriginal.getRapServidoresByMamEseSerFk2().getId().getMatricula());
		mamEmgServidorJn.setSerVinCodigo(mamServidorEmergenciaOriginal.getRapServidoresByMamEseSerFk2().getId().getVinCodigo());

		mamEmgServidorJnDAO.persistir(mamEmgServidorJn);

	}

	/**
	 * Insere um registro de MamEmgServidor, executando trigger de pre-insert
	 * 
	 * RN01 de #12174 - Manter cadastro de servidores
	 * 
	 * @param mamEmgServidor
	 * @throws ApplicationBusinessException
	 */
	private void inserir(MamEmgServidor mamEmgServidor) throws ApplicationBusinessException {
		Boolean exist = mamEmgServidorDAO.pesquisarServidoresEmergenciaExist(mamEmgServidor.getRapServidoresByMamEseSerFk1().getId().getVinCodigo(),
				mamEmgServidor.getRapServidoresByMamEseSerFk1().getId().getMatricula());
		if (exist) {
			throw new ApplicationBusinessException(MamEmgServidorRNExceptionCode.MENSAGEM_SERVIDOR_JA_CADASTRADO);
		}
		this.preInserir(mamEmgServidor);
		this.mamEmgServidorDAO.persistir(mamEmgServidor);
	}

	/**
	 * Atualiza um registro de MamEmgServidor
	 * 
	 * RN01 de #12174 - Manter cadastro de servidores
	 * 
	 * @param mamEmgServidor
	 */
	private void alterar(MamEmgServidor mamEmgServidor) throws ApplicationBusinessException {
		try {
			MamEmgServidor mamEmgServidorOriginal = this.mamEmgServidorDAO.obterOriginal(mamEmgServidor);
			if(mamEmgServidorOriginal == null){
				throw new ApplicationBusinessException(MamEmgServidorRNExceptionCode.OPTIMISTIC_LOCK);
			}
			if (CoreUtil.modificados(mamEmgServidor.getIndSituacao(), mamEmgServidorOriginal.getIndSituacao())) {
				this.inserirJournalServidorEmergencia(mamEmgServidorOriginal, DominioOperacoesJournal.UPD);
			}
			mamEmgServidorDAO.atualizar(mamEmgServidor);
			mamEmgServidorDAO.flush();
				
		} catch (BaseOptimisticLockException e) {
			throw new ApplicationBusinessException(MamEmgServidorRNExceptionCode.OPTIMISTIC_LOCK);
		}
	}

	/**
	 * Insere ou atualiza um registro de MamEmgServidor, executando trigger de pre-insert ou pre-update
	 * 
	 * RN01 de #12174 - Manter cadastro de servidores
	 * 
	 * @param mamEmgServidor
	 * @throws ApplicationBusinessException
	 */
	public void persistir(MamEmgServidor mamEmgServidor) throws ApplicationBusinessException {
		if (mamEmgServidor.getSeq() == null) {
			this.inserir(mamEmgServidor);
		} else {
			this.alterar(mamEmgServidor);
		}
	}

	/**
	 * Trigger de pre-insert de MamEmgServidor
	 * 
	 * RN02 de #12174 - Manter cadastro de servidores
	 * 
	 * @ORADB MAM_EMG_SERVIDORES. MAMT_ESE_BRI
	 * @param mamEmgServidor
	 * @throws ApplicationBusinessException
	 */
	private void preInserir(MamEmgServidor mamEmgServidor) throws ApplicationBusinessException {
		mamEmgServidor.setCriadoEm(new Date());		
		mamEmgServidor.setRapServidoresByMamEseSerFk2(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
	}

	/**
	 * Insere um registro de MamEmgServEspecialidade, executando trigger de pre-insert
	 * 
	 * RN03 de #12174 – Manter cadastro de servidores
	 * 
	 * @param mamEmgServEspecialidade
	 * @throws ApplicationBusinessException
	 */
	public void inserirMamEmgServEspecialidade(MamEmgServEspecialidade mamEmgServEspecialidade) throws ApplicationBusinessException {

		MamEmgServEspecialidade servEsp = mamEmgServEspecialidadeDAO.obterPorChavePrimaria(mamEmgServEspecialidade.getId());
		if (servEsp != null) {
			throw new ApplicationBusinessException(MamEmgServidorRNExceptionCode.MENSAGEM_ERRO_ESPECIALIDADE_SERVIDOR_JA_CADASTRADA);
		}
		MamEmgEspecialidades mamEmgEspecialidades = mamEmgEspecialidadesDAO.obterPorChavePrimaria(mamEmgServEspecialidade.getId().getEepEspSeq());

		// verifica se servidor ja nao foi excluido em outra sessao.
		MamEmgServidor mamEmgServidorOriginal = this.mamEmgServidorDAO.obterOriginal(mamEmgServEspecialidade.getMamEmgServidor());
		if (mamEmgServidorOriginal == null) {
			throw new ApplicationBusinessException(MamEmgServidorRNExceptionCode.OPTIMISTIC_LOCK);
		}

		mamEmgServEspecialidade.setMamEmgEspecialidades(mamEmgEspecialidades);
		mamEmgServEspecialidade.setIndProjetoEmei(DominioSimNao.N.toString());
		this.preInserir(mamEmgServEspecialidade);
		this.mamEmgServEspecialidadeDAO.persistir(mamEmgServEspecialidade);

	}

	/**
	 * Ativa/Inativa um registro de MamEmgServEspecialidade, executando trigger de pre-update
	 * 
	 * RN05 de #12174 – Manter cadastro de servidores
	 * 
	 * @param mamEmgServEspecialidade
	 * @throws ApplicationBusinessException
	 */
	public void ativarInativarMamEmgServEspecialidade(MamEmgServEspecialidade mamEmgServEspecialidade) throws ApplicationBusinessException {

		String sit = DominioSituacao.A.toString().equals(mamEmgServEspecialidade.getIndSituacao()) ? DominioSituacao.I.toString() : DominioSituacao.A
				.toString();
		mamEmgServEspecialidade.setIndSituacao(sit);
		
		mamEmgServEspecialidade.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));

		this.inserirJournalServidorEspecialidadeEmergencia(mamEmgServEspecialidade, DominioOperacoesJournal.UPD);

		this.mamEmgServEspecialidadeDAO.atualizar(mamEmgServEspecialidade);
	}

	/**
	 * Trigger de pre-insert de MamEmgServEspecialidade
	 * 
	 * RN04 de #12174 - Manter cadastro de servidores
	 * 
	 * @ORADB MAM_EMG_SERV_ESPECIALIDADES.MAMT_ESD_BRI
	 * @param mamEmgServEspecialidade
	 * @throws ApplicationBusinessException
	 */
	private void preInserir(MamEmgServEspecialidade mamEmgServEspecialidade) throws ApplicationBusinessException {
		mamEmgServEspecialidade.setCriadoEm(new Date());		
		mamEmgServEspecialidade.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
	}

	/**
	 * Insere um registro na journal de MamEmgServEspecialidade
	 * 
	 * @ORADB MAM_EMG_SERV_ESPECIALIDADES.MAMT_ESD_ARU
	 * @param mamEmgServEspecialidadeOriginal
	 * @param operacao
	 */
	private void inserirJournalServidorEspecialidadeEmergencia(MamEmgServEspecialidade mamEmgServEspecialidadeOriginal, DominioOperacoesJournal operacao) {
		MamEmgServEspecialidadeJn mamEmgServEspecialidadeJn = BaseJournalFactory.getBaseJournal(operacao, MamEmgServEspecialidadeJn.class, usuario.getLogin());
		mamEmgServEspecialidadeJn.setEepEspSeq(mamEmgServEspecialidadeOriginal.getId().getEepEspSeq());
		mamEmgServEspecialidadeJn.setEseSeq(mamEmgServEspecialidadeOriginal.getId().getEseSeq());
		mamEmgServEspecialidadeJn.setIndSituacao(mamEmgServEspecialidadeOriginal.getIndSituacao());
		mamEmgServEspecialidadeJn.setCriadoEm(mamEmgServEspecialidadeOriginal.getCriadoEm());		
		mamEmgServEspecialidadeJn.setSerMatricula(mamEmgServEspecialidadeOriginal.getRapServidores().getId().getMatricula());
		mamEmgServEspecialidadeJn.setSerVinCodigo(mamEmgServEspecialidadeOriginal.getRapServidores().getId().getVinCodigo());	
		mamEmgServEspecialidadeJn.setIndProjetoEmei(mamEmgServEspecialidadeOriginal.getIndProjetoEmei());
		mamEmgServEspecialidadeJnDAO.persistir(mamEmgServEspecialidadeJn);
	}

	/**
	 * Exclui um registro de MamEmgServEspecialidade, executando trigger de pre-delete
	 * 
	 * RN09 de #12174 - Manter cadastro de servidores
	 * 
	 * @param mamEmgServEspecialidade
	 * @throws ApplicationBusinessException
	 */
	public void excluirMamEmgServEspecialidade(MamEmgServEspecialidade mamEmgServEspecialidade) throws ApplicationBusinessException {

		if (!this.mamEmgServEspecialidadeDAO.contains(mamEmgServEspecialidade)) {
			mamEmgServEspecialidade = mamEmgServEspecialidadeDAO.obterPorChavePrimaria(mamEmgServEspecialidade.getId());
		}

		if (mamEmgServEspecialidade.getMamEmgServEspCoopes() != null && !mamEmgServEspecialidade.getMamEmgServEspCoopes().isEmpty()) {
			throw new ApplicationBusinessException(MamEmgServidorRNExceptionCode.MENSAGEM_ERRO_ESPECIALIDADE_SERVIDOR_POSSUI_COOPERACAO);
		}

		this.inserirJournalServidorEspecialidadeEmergencia(mamEmgServEspecialidade, DominioOperacoesJournal.DEL);

		this.mamEmgServEspecialidadeDAO.remover(mamEmgServEspecialidade);
	}

	/**
	 * Retorna as especialidades da emergencia ativas de acordo com o param especificado.
	 * 
	 * @param param
	 * @return
	 * @throws ServiceException
	 */
	public List<Especialidade> pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(String param) throws ApplicationBusinessException {

		Short seq = null;
		if (StringUtils.isNotEmpty(param) && CoreUtil.isNumeroShort(param.trim())) {
			seq = Short.parseShort(param);
		}

		List<Short> seqs = this.mamEmgEspecialidadesDAO.pesquisarAtivosPorSeq(seq, 100);

		if (seqs == null || seqs.isEmpty()) {
			return new ArrayList<Especialidade>();
		}

		try {
			return configuracaoService.pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaSeqs(param, seqs);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(MamEmgEspecialidadesRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}
	
	/**
	 * Retorna o número de especialidades da emergencia ativas de acordo com o param especificado.
	 * 
	 * @param param
	 * @return
	 * @throws ServiceException
	 */
	public Long pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaCount(String param) throws ApplicationBusinessException {

		Short seq = null;
		if (StringUtils.isNotEmpty(param) && CoreUtil.isNumeroShort(param.trim())) {
			seq = Short.parseShort(param);
		}

		List<Short> seqs = this.mamEmgEspecialidadesDAO.pesquisarAtivosPorSeq(seq, 100);

		if (seqs == null || seqs.isEmpty()) {
			return 0l;
		}

		try {
			return configuracaoService.pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaSeqsCount(param, seqs);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(MamEmgEspecialidadesRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}

	/**
	 * Insere um registro de MamEmgServEspCoop, executando trigger de pre-insert
	 * 
	 * RN036 de #12174 – Manter cadastro de servidores
	 * 
	 * @param mamEmgServEspCoop
	 * @throws ApplicationBusinessException
	 */
	public void inserirMamEmgServEspCoop(MamEmgServEspCoop mamEmgServEspCoop) throws ApplicationBusinessException {
		Integer eseSeq = mamEmgServEspCoop.getMamEmgServEspecialidade().getId().getEseSeq();
		Short eepEspSeq = mamEmgServEspCoop.getMamEmgServEspecialidade().getId().getEepEspSeq();
		Short tcoSeq = mamEmgServEspCoop.getMamTipoCooperacao().getSeq();
		Boolean exists = mamEmgServEspCoopDAO.pesquisarPorMamEmgServEspecialidadeMamTipoCooperacaoExists(eseSeq, eepEspSeq, tcoSeq);
		if (exists) {
			throw new ApplicationBusinessException(MamEmgServidorRNExceptionCode.MENSAGEM_ERRO_COOPERACAO_SERVIDOR_JA_CADASTRADA);
		}
		this.preInserir(mamEmgServEspCoop);
		this.mamEmgServEspCoopDAO.persistir(mamEmgServEspCoop);
	}

	/**
	 * Trigger de pre-insert de MamEmgServEspCoop
	 * 
	 * RN07 de #12174 - Manter cadastro de servidores
	 * 
	 * @ORADB MAM_EMG_SERV_ESP_COOPS.MAMT_ESC_BRI
	 * @param mamEmgServEspCoop
	 * @throws ApplicationBusinessException
	 */
	private void preInserir(MamEmgServEspCoop mamEmgServEspCoop) throws ApplicationBusinessException {
		mamEmgServEspCoop.setCriadoEm(new Date().toString());		
		mamEmgServEspCoop.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
	}

	/**
	 * Exclui um registro de MamEmgServEspCoop, executando trigger de pre-delete
	 * 
	 * RN08 de #12174 - Manter cadastro de servidores
	 * 
	 * @param mamEmgServEspCoop
	 * @throws ApplicationBusinessException
	 */
	public void excluirMamEmgServEspCoop(MamEmgServEspCoop mamEmgServEspCoop) throws ApplicationBusinessException {

		if (!this.mamEmgServEspCoopDAO.contains(mamEmgServEspCoop)) {
			mamEmgServEspCoop = mamEmgServEspCoopDAO.obterPorChavePrimaria(mamEmgServEspCoop.getSeq());
		}

		this.inserirJournalServidorEspecialidadeCoopEmergencia(mamEmgServEspCoop, DominioOperacoesJournal.DEL);

		this.mamEmgServEspCoopDAO.remover(mamEmgServEspCoop);
	}

	/**
	 * Insere um registro na journal de MamEmgServEspCoop
	 * 
	 * @ORADB MAM_EMG_SERV_ESPECIALIDADES.MAMT_ESD_ARD
	 * @param mamEmgServEspCoopOriginal
	 * @param operacao
	 */
	private void inserirJournalServidorEspecialidadeCoopEmergencia(MamEmgServEspCoop mamEmgServEspCoopOriginal, DominioOperacoesJournal operacao) {
		MamEmgServEspCoopJn mamEmgServEspCoopJn = BaseJournalFactory.getBaseJournal(operacao, MamEmgServEspCoopJn.class, usuario.getLogin());
		mamEmgServEspCoopJn.setSeq(mamEmgServEspCoopOriginal.getSeq());
		mamEmgServEspCoopJn.setEsdEepEspSeq(mamEmgServEspCoopOriginal.getMamEmgServEspecialidade().getId().getEepEspSeq());
		mamEmgServEspCoopJn.setEsdEseSeq(mamEmgServEspCoopOriginal.getMamEmgServEspecialidade().getId().getEseSeq());
		mamEmgServEspCoopJn.setTcoSeq(mamEmgServEspCoopOriginal.getMamTipoCooperacao().getSeq());
		mamEmgServEspCoopJn.setCriadoEm(mamEmgServEspCoopOriginal.getCriadoEm());
		mamEmgServEspCoopJn.setSerMatricula(mamEmgServEspCoopOriginal.getRapServidores().getId().getMatricula() );
		mamEmgServEspCoopJn.setSerVinCodigo(mamEmgServEspCoopOriginal.getRapServidores().getId().getVinCodigo());
		mamEmgServEspCoopJnDAO.persistir(mamEmgServEspCoopJn);
	}

}
