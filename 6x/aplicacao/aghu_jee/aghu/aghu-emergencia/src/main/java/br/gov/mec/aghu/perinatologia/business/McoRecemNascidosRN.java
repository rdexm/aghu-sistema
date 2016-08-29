package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoNascimentosId;
import br.gov.mec.aghu.model.McoRecemNascidoJn;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.service.IPacienteService;
import br.gov.mec.aghu.perinatologia.dao.McoNascimentosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoRecemNascidoJnDAO;
import br.gov.mec.aghu.perinatologia.dao.McoRecemNascidosDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.util.EmergenciaParametrosColunas;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class McoRecemNascidosRN extends BaseBusiness {
	private static final long serialVersionUID = 8301093533163822282L;

	private static final String INSERT = "INSERT";
	private static final String UPDATE = "UPDATE";
	private static final String DELETE = "DELETE";

	@Inject
	@QualificadorUsuario
	private Usuario usuario;

	@Inject
	private McoNascimentosDAO mcoNascimentosDAO;

	@Inject
	private McoRecemNascidosDAO mcoRecemNascidosDAO;

	@Inject
	private McoRecemNascidoJnDAO mcoRecemNascidoJnDAO;

	@Inject
	private IConfiguracaoService configuracaoService;

	@Inject
	private IPacienteService pacienteService;
	
	@Inject
	private RapServidoresDAO servidorDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Override
	protected Log getLogger() {
		return null;
	}

	private enum McoRecemNascidosRNExceptionCode implements BusinessExceptionCode {
		MCO_00565, //
		MCO_00566, //
		MENSAGEM_ERRO_PARAMETRO, //
		MCO_00733, //
		MCO_00709, //
		MENSAGEM_SERVICO_INDISPONIVEL, //
		MENSAGEM_ERRO_OBTER_PARAMETRO, //
		;
	}

	/**
	 * Atualizar MCO_RECEM_NASCIDOS
	 * 
	 * @param mcoRecemNascidos
	 * @param mcoRecemNascidosOriginal
	 * @throws ApplicationBusinessException
	 */
	public void atualizarRecemNascido(McoRecemNascidos mcoRecemNascidos, McoRecemNascidos mcoRecemNascidosOriginal,
			boolean origemMcoNascimento) throws ApplicationBusinessException {
		this.mcotRnaBru(mcoRecemNascidos);
		this.mcoRecemNascidosDAO.atualizar(mcoRecemNascidos);
		this.mcotRnaAru(mcoRecemNascidos, mcoRecemNascidosOriginal);
		this.mcotRnaAsu(mcoRecemNascidos, mcoRecemNascidosOriginal, false, origemMcoNascimento);
	}

	/**
	 * RN02 de #40539
	 * 
	 * @ORADB AGH.MCO_RECEM_NASCIDOS.MCOT_RNA_BRU – Trigger Before Update – Executar antes de efetuar update na tabela MCO_RECEM_NASCIDOS
	 * 
	 * @param mcoRecemNascidos
	 * @throws ApplicationBusinessException
	 */
	public void mcotRnaBru(McoRecemNascidos mcoRecemNascidos) throws ApplicationBusinessException {
		// Se o registro que está sendo alterado de MCO_RECEM_NASCIDOS tiver o campo ind_concluido = ‘S’ consistir o Apgar através da RN03.
		if (mcoRecemNascidos.getIndConcluido()) {
			this.rnRnapVerApgar(mcoRecemNascidos);
		}		
		
		mcoRecemNascidos.setServidor(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
	}

	/**
	 * RN03 de #40539
	 * 
	 * @ORADB MCOK_RNA_RN.RN_RNAP_VER_APGAR
	 * 
	 * @param mcoRecemNascidos
	 * @throws ApplicationBusinessException
	 */
	public void rnRnapVerApgar(McoRecemNascidos mcoRecemNascidos) throws ApplicationBusinessException {
		// 1. Se APGAR_1 é nulo OU APGAR_5 é nulo, apresentar a mensagem “MCO-00565” e cancelar o processamento.
		if (mcoRecemNascidos.getApgar1() == null || mcoRecemNascidos.getApgar5() == null) {
			throw new ApplicationBusinessException(McoRecemNascidosRNExceptionCode.MCO_00565);
		}
		// 2. Se APGAR_5 é menor que 7 E APGAR_10 é nulo, apresentar a mensagem “MCO-00566” e cancelar o processamento.
		if (mcoRecemNascidos.getApgar5().intValue() < 7 && mcoRecemNascidos.getApgar10() == null) {
			throw new ApplicationBusinessException(McoRecemNascidosRNExceptionCode.MCO_00566);
		}
	}

	/**
	 * RN04 de #40539
	 * 
	 * @ORADB AGH.MCO_RECEM_NASCIDOS.MCOT_RNA_ARU – Trigger After Update– Executar após efetuar update na tabela MCO_RECEM_NASCIDOS
	 * 
	 * @param mcoRecemNascidos
	 * @param mcoRecemNascidosOriginal
	 * @throws ApplicationBusinessException
	 */
	public void mcotRnaAru(McoRecemNascidos mcoRecemNascidos, McoRecemNascidos mcoRecemNascidosOriginal) {

		// Se alguma coluna da tabela MCO_RECEM_NASCIDOS foi modificada, realiza inserção na jornal através do I1, passando objeto original.
		boolean alterado = false;
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getApgar1(), mcoRecemNascidosOriginal.getApgar1());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getApgar5(), mcoRecemNascidosOriginal.getApgar5());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getApgar10(), mcoRecemNascidosOriginal.getApgar10());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndColetaSangueCordao(), mcoRecemNascidosOriginal.getIndColetaSangueCordao());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndAspiracao(), mcoRecemNascidosOriginal.getIndAspiracao());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndAspiracaoTet(), mcoRecemNascidosOriginal.getIndAspiracaoTet());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndO2Inalatorio(), mcoRecemNascidosOriginal.getIndO2Inalatorio());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndVentilacaoPorMascara(), mcoRecemNascidosOriginal.getIndVentilacaoPorMascara());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndVentilacaoTet(), mcoRecemNascidosOriginal.getIndVentilacaoTet());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndCateterismoVenoso(), mcoRecemNascidosOriginal.getIndCateterismoVenoso());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndMassCardiacaExt(), mcoRecemNascidosOriginal.getIndMassCardiacaExt());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndUrinou(), mcoRecemNascidosOriginal.getIndUrinou());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getServidor(), mcoRecemNascidosOriginal.getServidor());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndEvacuou(), mcoRecemNascidosOriginal.getIndEvacuou());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndCrede(), mcoRecemNascidosOriginal.getIndCrede());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndSurfactante(), mcoRecemNascidosOriginal.getIndSurfactante());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndLavadoGastrico(), mcoRecemNascidosOriginal.getIndLavadoGastrico());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getAspGastrVol(), mcoRecemNascidosOriginal.getAspGastrVol());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getAspGastrAspecto(), mcoRecemNascidosOriginal.getAspGastrAspecto());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndAspGastrOdorFetido(), mcoRecemNascidosOriginal.getIndAspGastrOdorFetido());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndAmamentado(), mcoRecemNascidosOriginal.getIndAmamentado());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getTempoRetornoMae(), mcoRecemNascidosOriginal.getTempoRetornoMae());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndConcluido(), mcoRecemNascidosOriginal.getIndConcluido());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getDthrNascimento(), mcoRecemNascidosOriginal.getDthrNascimento());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getEqpSeq(), mcoRecemNascidosOriginal.getEqpSeq());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getObservacao(), mcoRecemNascidosOriginal.getObservacao());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getIndObitoCo(), mcoRecemNascidosOriginal.getIndObitoCo());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getDiasRuptBolsa(), mcoRecemNascidosOriginal.getDiasRuptBolsa());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getHrsRuptBolsa(), mcoRecemNascidosOriginal.getHrsRuptBolsa());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getMinRuptBolsa(), mcoRecemNascidosOriginal.getMinRuptBolsa());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getTempoRetornoMin(), mcoRecemNascidosOriginal.getTempoRetornoMin());
		alterado = alterado || CoreUtil.modificados(mcoRecemNascidos.getMcoExameFisicoRns(), mcoRecemNascidosOriginal.getMcoExameFisicoRns());

		if (alterado){
			this.inserirJournal(mcoRecemNascidos, mcoRecemNascidosOriginal, DominioOperacoesJournal.UPD);
		}
	}

	/**
	 * I1 de de #40539
	 * 
	 * @param mcoRecemNascidos
	 * @param mcoRecemNascidosOriginal
	 * @param dominio
	 */
	private void inserirJournal(McoRecemNascidos mcoRecemNascidos, McoRecemNascidos mcoRecemNascidosOriginal,
			DominioOperacoesJournal dominio) {

		McoRecemNascidoJn mcoRecemNascidoJn = BaseJournalFactory.getBaseJournal(dominio, McoRecemNascidoJn.class, usuario.getLogin());

		mcoRecemNascidoJn.setApgar1(mcoRecemNascidosOriginal.getApgar1());
		mcoRecemNascidoJn.setApgar10(mcoRecemNascidosOriginal.getApgar10());
		mcoRecemNascidoJn.setApgar5(mcoRecemNascidosOriginal.getApgar5());
		mcoRecemNascidoJn.setAspGastrAspecto(mcoRecemNascidosOriginal.getAspGastrAspecto());
		mcoRecemNascidoJn.setAspGastrVol(mcoRecemNascidosOriginal.getAspGastrVol());
		mcoRecemNascidoJn.setCriadoEm(mcoRecemNascidosOriginal.getCriadoEm());
		mcoRecemNascidoJn.setDiasRuptBolsa(mcoRecemNascidosOriginal.getDiasRuptBolsa());
		mcoRecemNascidoJn.setDthrNascimento(mcoRecemNascidosOriginal.getDthrNascimento());
		mcoRecemNascidoJn.setEqpSeq(mcoRecemNascidosOriginal.getEqpSeq());
		mcoRecemNascidoJn.setGsoPacCodigo(mcoRecemNascidosOriginal.getId().getGsoPacCodigo());
		mcoRecemNascidoJn.setGsoSeqp(mcoRecemNascidosOriginal.getId().getGsoSeqp());
		mcoRecemNascidoJn.setHrsRuptBolsa(mcoRecemNascidosOriginal.getHrsRuptBolsa());
		mcoRecemNascidoJn.setIndAmamentado(mcoRecemNascidosOriginal.getIndAmamentado());
		mcoRecemNascidoJn.setIndAspGastrOdorFetido(mcoRecemNascidosOriginal.getIndAspGastrOdorFetido());
		mcoRecemNascidoJn.setIndAspiracao(mcoRecemNascidosOriginal.getIndAspiracao());
		mcoRecemNascidoJn.setIndAspiracaoTet(mcoRecemNascidosOriginal.getIndAspiracaoTet());
		mcoRecemNascidoJn.setIndCateterismoVenoso(mcoRecemNascidosOriginal.getIndCateterismoVenoso());
		mcoRecemNascidoJn.setIndColetaSangueCordao(mcoRecemNascidosOriginal.getIndColetaSangueCordao());
		mcoRecemNascidoJn.setIndConcluido(mcoRecemNascidosOriginal.getIndConcluido());
		mcoRecemNascidoJn.setIndCrede(mcoRecemNascidosOriginal.getIndCrede());
		mcoRecemNascidoJn.setIndEvacuou(mcoRecemNascidosOriginal.getIndEvacuou());
		mcoRecemNascidoJn.setIndLavadoGastrico(mcoRecemNascidosOriginal.getIndLavadoGastrico());
		mcoRecemNascidoJn.setIndMassCardiacaExt(mcoRecemNascidosOriginal.getIndMassCardiacaExt());
		mcoRecemNascidoJn.setIndO2Inalatorio(mcoRecemNascidosOriginal.getIndO2Inalatorio());
		mcoRecemNascidoJn.setIndObitoCo(mcoRecemNascidosOriginal.getIndObitoCo());
		mcoRecemNascidoJn.setIndSurfactante(mcoRecemNascidosOriginal.getIndSurfactante());
		mcoRecemNascidoJn.setIndUrinou(mcoRecemNascidosOriginal.getIndUrinou());
		mcoRecemNascidoJn.setIndVentilacaoPorMascara(mcoRecemNascidosOriginal.getIndVentilacaoPorMascara());
		mcoRecemNascidoJn.setIndVentilacaoTet(mcoRecemNascidosOriginal.getIndVentilacaoTet());
		mcoRecemNascidoJn.setMinRuptBolsa(mcoRecemNascidosOriginal.getMinRuptBolsa());
		mcoRecemNascidoJn.setObservacao(mcoRecemNascidosOriginal.getObservacao());
		mcoRecemNascidoJn.setSeqp(mcoRecemNascidosOriginal.getId().getSeqp());
		mcoRecemNascidoJn.setSerMatricula(mcoRecemNascidosOriginal.getServidor().getId().getMatricula() );
		mcoRecemNascidoJn.setSerVinCodigo(mcoRecemNascidosOriginal.getServidor().getId().getVinCodigo());
		mcoRecemNascidoJn.setTempoRetornoMae(mcoRecemNascidosOriginal.getTempoRetornoMae());
		mcoRecemNascidoJn.setTempoRetornoMin(mcoRecemNascidosOriginal.getTempoRetornoMin());

		this.mcoRecemNascidoJnDAO.persistir(mcoRecemNascidoJn);
	}

	/**
	 * RN05 de #40539
	 * 
	 * @ORADB AGH.MCO_RECEM_NASCIDOS MCOT_RNA_ASU – Trigger After Update– Executar após efetuar update na tabela MCO_RECEM_NASCIDOS
	 * 
	 * @param mcoRecemNascidos
	 * @param mcoRecemNascidosOriginal
	 * @param inclusao
	 * @throws ApplicationBusinessException
	 */
	public void mcotRnaAsu(McoRecemNascidos mcoRecemNascidos, McoRecemNascidos mcoRecemNascidosOriginal, boolean inclusao,
			boolean origemMcoNascimento) throws ApplicationBusinessException {
		if (inclusao) {
			// Se for inclusão de recém-nascido executar a procedures mcoK_rna.process_rna_rows('INSERT') através da RN06, passando o
			// parâmetro “INSERT”.
			this.processRnaRows(mcoRecemNascidos, mcoRecemNascidosOriginal, INSERT, origemMcoNascimento);
		} else {
			// Se for alteração de recém-nascido executar a procedures mcoK_rna.process_rna_rows('UPDATE') através da RN06, passando o
			// parâmetro “UPDATE”.
			this.processRnaRows(mcoRecemNascidos, mcoRecemNascidosOriginal, UPDATE, origemMcoNascimento);
		}

	}

	/**
	 * RN06 de #40539
	 * 
	 * @ORADB MCOK_RNA.PROCESS_RNA_ROWS – Procedure da tabela MCO_RECEM_NASCIDOS
	 * 
	 * @param mcoRecemNascidos
	 * @param mcoRecemNascidosOriginal
	 * @param operacao
	 * @throws ApplicationBusinessException
	 */
	public void processRnaRows(McoRecemNascidos mcoRecemNascidos, McoRecemNascidos mcoRecemNascidosOriginal, String operacao,
			boolean origemMcoNascimento) throws ApplicationBusinessException {
		// Se o parâmetro de entrada for diferente de “DELETE”, recuperar os registros da tabela MCO_RECEM_NASCIDOS
		if (!DELETE.equals(operacao)) {
			// Chamar a Enforce MCOP_ENFORCE_RNA_RULES através da RN07.
			this.mcopEnforceRnaRules(mcoRecemNascidos, mcoRecemNascidosOriginal, origemMcoNascimento);
		}
	}

	/**
	 * RN07 de #40539
	 * 
	 * @ORADB MCOP_ENFORCE_RNA_RULES – Enforces da tabela MCO_RECEM_NASCIDOS
	 * 
	 * @param mcoRecemNascidos
	 * @param mcoRecemNascidosOriginal
	 * @throws ApplicationBusinessException
	 */
	public void mcopEnforceRnaRules(McoRecemNascidos mcoRecemNascidos, McoRecemNascidos mcoRecemNascidosOriginal,
			boolean origemMcoNascimento) throws ApplicationBusinessException {
		// Se houve alteração no campo MCO_RECEM_NASCIDOS.DTHR_NASCIMENTO executar os seguintes passos:
		if (CoreUtil.modificados(mcoRecemNascidos.getDthrNascimento(), mcoRecemNascidosOriginal.getDthrNascimento())) {
			// 1. Validar a data de nascimento através da RN08 passando como parâmetro o campo MCO_RECEM_NASCIDOS.DTHR_NASCIMENTO.
			this.rnRnapVerData(mcoRecemNascidos);

			// 2. Se a origem da alteração na data de nascimento em MCO_RECEM_NASCIDOS não for através do update ou do insert da
			// MCO_NASCIMENTOS executar a RN09 passando o sequencial da gestante, o código da paciente e o sequencial do recém-nascido para
			// alterar a data de nascimento em MCO_NASCIMENTOS.
			if (!origemMcoNascimento) {
				this.rnRnapAtuNsc(mcoRecemNascidos);
			}

			// 3. Se a origem da alteração na data de nascimento MCO_RECEM_NASCIDOS não for através de um update na AIP_NASCIMENTO executar
			// a RN10 passando a nova data de nascimento do recém-nascido, o código da paciente e a data a ser alterada do recém-nascido.
			if (origemMcoNascimento) {
				this.rnRnapAtuPaciente(mcoRecemNascidos, mcoRecemNascidosOriginal);
			}
		}
	}

	/**
	 * RN08 de #40539
	 * 
	 * @ORADB MCOK_RNA_RN.RN_RNAP_VER_DATA – Procedure de validação da data
	 * 
	 * @param mcoRecemNascidos
	 * @throws ApplicationBusinessException
	 */
	public void rnRnapVerData(McoRecemNascidos mcoRecemNascidos) throws ApplicationBusinessException {

		// Obter o parâmetro de sistema “P_DIAS_CONSISTE_DATA” através do serviço #34780. Se o parâmetro não for encontrado ou o valor do
		// campo VLR_NUMERICO for nulo ou o serviço estiver indisponível disparar a mensagem “MENSAGEM_ERRO_PARAMETRO” passando o nome do
		// parâmetro e cancelar o processamento.
		// Object pDiasConsisteData =
		this.buscarParametroPorNome(EmergenciaParametrosEnum.P_DIAS_CONSISTE_DATA.toString(), EmergenciaParametrosColunas.VLR_NUMERICO.toString());

		// Colocar #TODO para quando for chamada pela procedure “@ORADB AIPP_SUBS_PAC_GESTA” incluir o seguinte teste:
		// Incluir no #TODO: Se a origem da chamada da rotina for diferente da procedure “@ORADB AIPP_SUBS_PAC_GESTA” efetuar o seguinte
		// teste:
		// Se a DTHR_NASCIMENTO for menor que (Data Atual menos VLR_NUMERICO do parâmetro), apresentar a mensagem “MCO-00733”.

		// Se a DTHR_NASCIMENTO for maior que (Data Atual mais(+) 0.007), apresentar a mensagem “MCO-00709”.
		Date referencia = DateUtil.adicionaDiasFracao(new Date(), 0.007f);
		if (CoreUtil.isMaiorDatas(mcoRecemNascidos.getDthrNascimento(), referencia)) {
			throw new ApplicationBusinessException(McoRecemNascidosRNExceptionCode.MCO_00709);
		}
	}

	/**
	 * RN09 de #40539
	 * 
	 * @ORADB MCOK_RNA_RN.RN_RNAP_ATU_NASC – Atualização da data de nascimento na MCO_NASCIMENTOS
	 * 
	 * @param mcoRecemNascidos
	 * @param mcoRecemNascidosOriginal
	 * @throws ApplicationBusinessException
	 */
	public void rnRnapAtuNsc(McoRecemNascidos mcoRecemNascidos) throws ApplicationBusinessException {

		// Buscar a data de nascimento em MCO_NASCIMENTOS para o recém-nascido passado por parâmetro através da C1.
		McoNascimentosId id = new McoNascimentosId();
		id.setGsoPacCodigo(mcoRecemNascidos.getId().getGsoPacCodigo());
		id.setGsoSeqp(mcoRecemNascidos.getId().getGsoSeqp());
		id.setSeqp(mcoRecemNascidos.getId().getSeqp().intValue());
		McoNascimentos mcoNascimento = this.mcoNascimentosDAO.obterPorChavePrimaria(id);

		// Se encontrar dados e a data de nascimento da C1.DTHR_NASCIMENTO for diferente da data de nascimento do recém-nascido
		// MCO_RECEM_NASCIDOS.DTHR_NASCIMENTO efetuar UPDATE da data de nascimento em MCO_NASCIMENTOS através da U2. Se não houver
		// nascimento não faz nada.
		if (mcoNascimento != null && CoreUtil.modificados(mcoNascimento.getDthrNascimento(), mcoRecemNascidos.getDthrNascimento())) {
			mcoNascimento.setDthrNascimento(mcoRecemNascidos.getDthrNascimento());
			this.mcoNascimentosDAO.atualizar(mcoNascimento);
		}
	}

	/**
	 * RN10 de #40539
	 * 
	 * @ORADB MCOK_RNA_RN.RN_RNAP_ATU_PACIENTE – Atualização da data de nascimento do paciente
	 * 
	 * @param mcoRecemNascidos
	 * @param mcoRecemNascidosOriginal
	 * @throws ApplicationBusinessException
	 */
	public void rnRnapAtuPaciente(McoRecemNascidos mcoRecemNascidos, McoRecemNascidos mcoRecemNascidosOriginal)
			throws ApplicationBusinessException {
		// Recuperar a data de início do atendimento através da consulta C2 passando a nova data de nascimento do recém-nascido, o código da
		// paciente e a data a ser alterada do recém-nascido.
		Date dtHrInicioAtendimento = this.obterDataInicioAtendimentoPorPaciente(mcoRecemNascidos.getId().getGsoPacCodigo(),
				mcoRecemNascidosOriginal.getDthrNascimento());

		// Se a data de início do atendimento (C2) for diferente da data nova do nascimento do recém-nascido, atualizar a data início da
		// tabela de atendimento para a nova data de nascimento do recém-nascido através do U3 passando a nova data de nascimento do
		// recém-nascido, o código da paciente e a data a ser alterada do recém-nascido, se não houver atendimento não atualiza.
		if (CoreUtil.modificados(dtHrInicioAtendimento, mcoRecemNascidos.getDthrNascimento())) {
			this.atualizarAtendimentoDthrNascimento(mcoRecemNascidos.getId().getGsoPacCodigo(),
					dtHrInicioAtendimento, mcoRecemNascidos.getDthrNascimento());
		}

		// Atualizar a data de nascimento na tabela de pacientes AIP_PACIENTES através da U4 sendo passado a nova data de nascimento do
		// recém-nascido e o código do paciente, se não existir paciente não faz nada..
		this.atualizarPacienteDthrNascimento(mcoRecemNascidos.getId().getGsoPacCodigo(), mcoRecemNascidos.getDthrNascimento());
	}

	/**
	 * C2 de #40539
	 * 
	 * @param pacCodigo
	 * @param dataNascimento
	 * @return
	 */
	private Date obterDataInicioAtendimentoPorPaciente(Integer pacCodigo, Date dataNascimento) throws ApplicationBusinessException {
		Date retorno = null;
		try {
			retorno = this.configuracaoService.obterDataInicioAtendimentoPorPaciente(pacCodigo, dataNascimento);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(McoRecemNascidosRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(McoRecemNascidosRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return retorno;
	}

	/**
	 * U3 de #40539
	 * 
	 * @param pacCodigo
	 * @param dataNascimento
	 * @param novaDataNascimento
	 * @return
	 */
	private void atualizarAtendimentoDthrNascimento(Integer pacCodigo, Date dataNascimento, Date novaDataNascimento)
			throws ApplicationBusinessException {
		try {
			this.configuracaoService.atualizarAtendimentoDthrNascimento(pacCodigo, dataNascimento, novaDataNascimento);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(McoRecemNascidosRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(McoRecemNascidosRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}

	/**
	 * U4 de #40539
	 * 
	 * @param pacCodigo
	 * @param dataNascimento
	 * @param novaDataNascimento
	 * @return
	 */
	private void atualizarPacienteDthrNascimento(Integer pacCodigo, Date novaDataNascimento) throws ApplicationBusinessException {
		try {
			this.pacienteService.atualizarPacienteDthrNascimento(pacCodigo, novaDataNascimento);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(McoRecemNascidosRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(McoRecemNascidosRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}

	private Object buscarParametroPorNome(String nome, String coluna) throws ApplicationBusinessException {
		Object retorno = null;
		try {
			retorno = this.parametroFacade.obterAghParametroPorNome(nome, coluna);
			if (retorno == null) {
				throw new ApplicationBusinessException(McoRecemNascidosRNExceptionCode.MENSAGEM_ERRO_OBTER_PARAMETRO, nome);
			}
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(McoRecemNascidosRNExceptionCode.MENSAGEM_ERRO_OBTER_PARAMETRO, nome);
		}
		return retorno;
	}
}
