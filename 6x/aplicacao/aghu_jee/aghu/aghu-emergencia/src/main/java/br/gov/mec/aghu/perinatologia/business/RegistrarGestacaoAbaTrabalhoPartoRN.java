package br.gov.mec.aghu.perinatologia.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioFormaRupturaBolsaRota;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.emergencia.vo.MedicamentoVO;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.McoAtendTrabPartos;
import br.gov.mec.aghu.model.McoBolsaRotas;
import br.gov.mec.aghu.model.McoBolsaRotasJn;
import br.gov.mec.aghu.model.McoMedicamentoTrabPartos;
import br.gov.mec.aghu.model.McoMedicamentoTrabPartosId;
import br.gov.mec.aghu.model.McoMedicamentoTrabPartosJn;
import br.gov.mec.aghu.model.McoTrabPartos;
import br.gov.mec.aghu.model.McoTrabPartosJn;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.perinatologia.dao.McoAtendTrabPartosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoBolsaRotasDAO;
import br.gov.mec.aghu.perinatologia.dao.McoBolsaRotasJnDAO;
import br.gov.mec.aghu.perinatologia.dao.McoMedicamentoTrabPartosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoMedicamentoTrabPartosJnDAO;
import br.gov.mec.aghu.perinatologia.dao.McoTrabPartosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoTrabPartosJnDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.service.IRegistroColaboradorService;
import br.gov.mec.aghu.registrocolaborador.vo.RapServidorConselhoVO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;

/**
 * @author felipe.rocha
 */
@Stateless
public class RegistrarGestacaoAbaTrabalhoPartoRN extends BaseBusiness {

	private static final long serialVersionUID = -3423984755101821178L;

	@Inject
	private McoMedicamentoTrabPartosDAO mcoMedicamentoTrabPartosDAO;

	@Inject
	private McoBolsaRotasDAO bolsaRotasDAO;

	@Inject
	private McoBolsaRotasJnDAO bolsaRotasJnDAO;

	@Inject
	private McoTrabPartosDAO mcoTrabPartosDAO;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private McoTrabPartosJnDAO mcoTrabPartosJnDAO;

	@Inject
	private McoMedicamentoTrabPartosJnDAO mcoMedicamentoTrabPartosJnDAO;
	
	@Inject
	private McoAtendTrabPartosDAO mcoAtendTrabPartosDAO;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private IRegistroColaboradorService registroColaboradorService;
	
	@Inject
	private RapServidoresDAO servidorDAO;
	
	@Override
	protected Log getLogger() {
		return null;
	}

	private enum RegistrarGestacaoRNExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_SERVICO_INDISPONIVEL,  
		MENSAGEM_TRABALHO_PARTO_MCO_00087, 
		MENSAGEM_TRABALHO_PARTO_MCO_00516, 
		MENSAGEM_TRABALHO_PARTO_MCO_00521, 
		MENSAGEM_TRABALHO_PARTO_MCO_00624, 
		MENSAGEM_TRABALHO_PARTO_ERRO_RESP_IGUAIS, 
		ERRO_DATA_HORA_OBRIGATORIA_AMNIOTOMIA,
		ERRO_DATA_HORA_IGNORADO_OBRIGATORIO_AMNIORREXIS, 
		MENSAGEM_TRABALHO_PARTO_MCO_00509,
		MENSAGEM_TRABALHO_PARTO_MCO_00520
	}

	

	public List<MedicamentoVO> pesquisarTrabPartoMedicamentos(
			Integer gsoPacCodigo, Short gsoSeqp) {
		List<br.gov.mec.aghu.farmacia.vo.MedicamentoVO> medicamentos = null;
		List<Integer> medMatCodigos = mcoMedicamentoTrabPartosDAO
				.pesquisarMedMatCodigos(gsoPacCodigo, gsoSeqp);
		if (medMatCodigos.size() != 0) {
			medicamentos= farmaciaFacade.pesquisarMedicamentoPorCodigosEmergencia(medMatCodigos);
		}
		List<McoMedicamentoTrabPartos> mcoMedicamentosTrabPartos = mcoMedicamentoTrabPartosDAO
				.pesquisarMcoMedicamentoTrabPartos(gsoPacCodigo, gsoSeqp);

		List<MedicamentoVO> result = new ArrayList<MedicamentoVO>(0);

		for (McoMedicamentoTrabPartos medicamentoTrabParto : mcoMedicamentosTrabPartos) {
			final MedicamentoVO vo = new MedicamentoVO();

			vo.setMatCodigo(medicamentoTrabParto.getId().getMedMatCodigo());
			vo.setMatCodigo(medicamentoTrabParto.getId().getMedMatCodigo());
			vo.setDataHoraInicial(medicamentoTrabParto.getDataHoraInicial());
			vo.setDataHoraFinal(medicamentoTrabParto.getDataHoraFinal());
			vo.setObservacao(medicamentoTrabParto.getObservacao());
			if (medicamentos != null) {
				br.gov.mec.aghu.farmacia.vo.MedicamentoVO medVo = (br.gov.mec.aghu.farmacia.vo.MedicamentoVO) CollectionUtils
						.find(medicamentos, new Predicate() {
							public boolean evaluate(Object object) {
								return ((br.gov.mec.aghu.farmacia.vo.MedicamentoVO) object)
										.getMatCodigo().equals(vo.getMatCodigo());
							}
						});
				vo.setDescricao(medVo.getDescricao());
			}
			result.add(vo);
		}
		return result;
	}

	
	public boolean verificarSumarioPrevio(Short gsoSeqp, Integer gsoPacCodigo){
		List<McoAtendTrabPartos> atend = this.mcoAtendTrabPartosDAO.listarAtendPartosPorId(gsoSeqp, gsoPacCodigo);
		if(atend!=null && !atend.isEmpty() && atend.size()>0){
			return true;
		}
		return false;
	}
	
	
	
	public void gravarBolsaRotas(McoBolsaRotas mcoBolsaRotas)
			throws ApplicationBusinessException {

		Date dthrRompimento = mcoBolsaRotas.getDthrRompimento();

		validarMcoBolsaRotas(dthrRompimento,
				mcoBolsaRotas.getDominioFormaRuptura());

		McoBolsaRotas original = bolsaRotasDAO.obterOriginal(mcoBolsaRotas
				.getId());
		boolean bolsaRotasModificada = false;
		if (original != null) {
			bolsaRotasModificada = verificarMcoBolsaRotasAtualizada(mcoBolsaRotas, original);
		}
		if (bolsaRotasModificada && original != null) {
			bolsaRotasDAO.merge(mcoBolsaRotas);
			insereMcoBolsaRotasJn(original);
		} 
		if(original == null) {
			mcoBolsaRotas.setSerMatricula(usuario.getMatricula());
			mcoBolsaRotas.setSerVinCodigo(usuario.getVinculo());
			mcoBolsaRotas.setCriadoEm(new Date());
			mcoBolsaRotas.setIndAmnioscopia(false);
			bolsaRotasDAO.persistir(mcoBolsaRotas);
		}

	}

	/**
	 * 
	 * RN05 de #25663- REGISTRAR TRABALHO DE PARTO
	 * 
	 * @ORADB MCO_BOLSA_ROTAS.MCOT_BSR_ARU – trigger after update
	 * 
	 * 
	 * @param McoBolsaRotas
	 *            mcoBolsaRotas
	 */
	private void insereMcoBolsaRotasJn(McoBolsaRotas mcoBolsaRotas) {
		McoBolsaRotasJn bolsaRotasJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, McoBolsaRotasJn.class, usuario.getLogin());

		bolsaRotasJn.setDthrRompimento(mcoBolsaRotas.getDthrRompimento());
		bolsaRotasJn.setFormaRuptura(mcoBolsaRotas.getFormaRuptura());
		bolsaRotasJn.setIndAmnioscopia(mcoBolsaRotas.getIndAmnioscopia());
		bolsaRotasJn.setLiquidoAmniotico(mcoBolsaRotas.getLiquidoAmniotico() == null ? null : mcoBolsaRotas.getLiquidoAmniotico().toString());
		bolsaRotasJn.setIndOdorFetido(mcoBolsaRotas.getIndOdorFetido());
		bolsaRotasJn.setCriadoEm(mcoBolsaRotas.getCriadoEm());
		bolsaRotasJn.setGsoSeqp(mcoBolsaRotas.getId().getSeqp());
		bolsaRotasJn.setGsoPacCodigo(mcoBolsaRotas.getId().getPacCodigo());
		bolsaRotasJn.setSerMatricula(mcoBolsaRotas.getSerMatricula());
		bolsaRotasJn.setSerVinCodigo(mcoBolsaRotas.getSerVinCodigo());
		this.bolsaRotasJnDAO.persistir(bolsaRotasJn);
	}

	/**
	 * 
	 * RN04 de #25663- REGISTRAR TRABALHO DE PARTO
	 * 
	 * @ORADB MCO_BOLSA_ROTAS.MCOT_BSR_BRU – trigger before update
	 * 
	 * 
	 * @param Date
	 *            dthrRompimento
	 * @param DominioFormaRupturaBolsaRota
	 *            formaRuptura
	 */
	private void validarMcoBolsaRotas(Date dthrRompimento,
			DominioFormaRupturaBolsaRota formaRuptura)
			throws ApplicationBusinessException {
		validarHoraDataRompimento(dthrRompimento);

		validarFormaRupturaAminiotica(formaRuptura, dthrRompimento);
	}

	private boolean verificarMcoBolsaRotasAtualizada(
			McoBolsaRotas mcoBolsaRotas, McoBolsaRotas original) {
		return CoreUtil.modificados(mcoBolsaRotas.getDominioFormaRuptura(),
				original.getDominioFormaRuptura())
				|| CoreUtil.modificados(mcoBolsaRotas.getDthrRompimento(),
						original.getDthrRompimento())
				|| CoreUtil.modificados(mcoBolsaRotas.getLiquidoAmniotico(),
						original.getLiquidoAmniotico())
				|| CoreUtil.modificados(mcoBolsaRotas.getIndDataHoraIgnorada(),
						original.getIndDataHoraIgnorada())
				|| CoreUtil.modificados(mcoBolsaRotas.getIndOdorFetido(),
						original.getIndOdorFetido());
	}

	private void validarFormaRupturaAminiotica(
			DominioFormaRupturaBolsaRota formaRuptura, Date dthrRompimento)
			throws ApplicationBusinessException {
		if (formaRuptura != null
				&& formaRuptura.equals(DominioFormaRupturaBolsaRota.Amniotomia)
				&& dthrRompimento == null) {
			throw new ApplicationBusinessException(
					RegistrarGestacaoRNExceptionCode.MENSAGEM_TRABALHO_PARTO_MCO_00624);
		}
	}

	private void validarHoraDataRompimento(Date dthrRompimento)
			throws ApplicationBusinessException {

		if (dthrRompimento != null) {
			Date hoje = new Date();
			Date trintaDias = DateUtils.addDays(hoje, - 30);
			boolean dataRompimentoMaiorTrinta = trintaDias.after(dthrRompimento);
			Date dthrRompimentoTrunc = DateUtil.truncaData(dthrRompimento);
			Date hojeTrunc = DateUtil.truncaData(hoje);
			if (!DateUtil.isDatasIguais(hojeTrunc, dthrRompimentoTrunc) && dataRompimentoMaiorTrinta || dthrRompimentoTrunc.after(hojeTrunc)) {
				throw new ApplicationBusinessException(
						RegistrarGestacaoRNExceptionCode.MENSAGEM_TRABALHO_PARTO_MCO_00521);
			}
		}
	}

	public void gravarTrabalhoParto(McoTrabPartos mcoTrabPartos,
			McoBolsaRotas bolsaRotas) throws ApplicationBusinessException {

		verificarResponsaveisDiferentes(mcoTrabPartos);
		verificarDataHoraObrigatoriaAminiotica(bolsaRotas);
		verificarDataHoraIgnoradoObrigatorioAmniorrexis(bolsaRotas);
		verificarDataIndicacoesCtg(mcoTrabPartos);
		verificarObrigatoriedadeCamposCtg(mcoTrabPartos);
		
		McoTrabPartos original =  mcoTrabPartosDAO.obterOriginal(mcoTrabPartos.getId());
		gravarBolsaRotas(bolsaRotas);
		if (original != null){
			if (atualizaJornal(original, mcoTrabPartos)) {
				insereJornal(original, DominioOperacoesJournal.UPD);
			}
			mcoTrabPartos.setSerMatricula(usuario.getMatricula());
			mcoTrabPartos.setSerVinCodigo(usuario.getVinculo());			
			
			this.mcoTrabPartosDAO.merge(mcoTrabPartos);

		}else{
			mcoTrabPartos.setCriadoEm(new Date());
			mcoTrabPartos.setSerMatricula(usuario.getMatricula());
			mcoTrabPartos.setSerVinCodigo(usuario.getVinculo());
			this.mcoTrabPartosDAO.persistir(mcoTrabPartos);
		}

	}

	private void verificarDataIndicacoesCtg(McoTrabPartos mcoTrabPartos) throws ApplicationBusinessException {
		if((mcoTrabPartos.getDthriniCtg() != null && mcoTrabPartos.getIndicacoesCtg() == null) ||
				(mcoTrabPartos.getDthriniCtg() == null && mcoTrabPartos.getIndicacoesCtg() != null)){
			throw new ApplicationBusinessException(RegistrarGestacaoRNExceptionCode.MENSAGEM_TRABALHO_PARTO_MCO_00520);
		}
		
	}


	/**
	 * 
	 * RN13 de #25663- REGISTRAR TRABALHO DE PARTO
	 * 
	 * @ORADB MCO_TRAB_PARTOS.MCOT_TPA_BRI – trigger before insert
	 * 
	 * 
	 * @param McoTrabPartos
	 *            mcoTrabPartos
	 */

	private void verificarObrigatoriedadeCamposCtg(McoTrabPartos mcoTrabPartos)
			throws ApplicationBusinessException {
		if (mcoTrabPartos.getDthriniCtg() == null
				&& mcoTrabPartos.getIndicacoesCtg() == null && mcoTrabPartos.getTipoParto() == null &&
				mcoTrabPartos.getObservacao() == null) {
			throw new ApplicationBusinessException(
					RegistrarGestacaoRNExceptionCode.MENSAGEM_TRABALHO_PARTO_MCO_00509);
		}
	}

	private void verificarDataHoraIgnoradoObrigatorioAmniorrexis(
			McoBolsaRotas bolsaRotas) throws ApplicationBusinessException {
		DominioFormaRupturaBolsaRota dominioFormaRuptura = bolsaRotas
				.getDominioFormaRuptura();
		if (dominioFormaRuptura != null
				&& dominioFormaRuptura
						.equals(DominioFormaRupturaBolsaRota.Amniorrexis)
				&& bolsaRotas.getDthrRompimento() == null) {
			throw new ApplicationBusinessException(
					RegistrarGestacaoRNExceptionCode.ERRO_DATA_HORA_IGNORADO_OBRIGATORIO_AMNIORREXIS);
		}
	}

	private void verificarDataHoraObrigatoriaAminiotica(McoBolsaRotas bolsaRotas)
			throws ApplicationBusinessException {
		DominioFormaRupturaBolsaRota dominioFormaRuptura = bolsaRotas
				.getDominioFormaRuptura();
		if ((dominioFormaRuptura != null && dominioFormaRuptura
				.equals(DominioFormaRupturaBolsaRota.Amniotomia))
				&& bolsaRotas.getDthrRompimento() == null) {
			throw new ApplicationBusinessException(
					RegistrarGestacaoRNExceptionCode.ERRO_DATA_HORA_OBRIGATORIA_AMNIOTOMIA);
		}
	}

	private void verificarResponsaveisDiferentes(McoTrabPartos mcoTrabPartos)
			throws ApplicationBusinessException {
		Integer servidorMatriculaIndicado = mcoTrabPartos
				.getSerMatriculaIndicado();
		Integer servidorMatriculaIndicado2 = mcoTrabPartos
				.getSerMatriculaIndicado2();

		Short servidorVinCodigoIndicado = mcoTrabPartos
				.getSerVinCodigoIndicado();
		Short servidorVinCodigoIndicado2 = mcoTrabPartos
				.getSerVinCodigoIndicado2();

		if (servidorMatriculaIndicado != null
				&& servidorMatriculaIndicado2 != null
				&& servidorVinCodigoIndicado != null
				&& servidorVinCodigoIndicado2 != null) {
			if ((servidorMatriculaIndicado.equals(servidorMatriculaIndicado2) && servidorVinCodigoIndicado
					.equals(servidorVinCodigoIndicado2))) {
				throw new ApplicationBusinessException(
						RegistrarGestacaoRNExceptionCode.MENSAGEM_TRABALHO_PARTO_ERRO_RESP_IGUAIS);
			}
		}
	}

	/**
	 * 
	 * RN11 de #25663- REGISTRAR TRABALHO DE PARTO
	 * 
	 * @ORADB MCO_MEDICAMENTO_TRAB_PARTOS.MCOT_MTR_ARD – trigger after delete
	 * 
	 * 
	 * @param Integer matCodigo
	 * @param Short gsoSeqp
	 * @param Integer gsoPacCodigo
	 */
	public void excluirMedicamento(Integer matCodigo, Short gsoSeqp,Integer gsoPacCodigo) {
		McoMedicamentoTrabPartos mcoMedicamentoTrabPartos = this.mcoMedicamentoTrabPartosDAO.buscarMcoMedicamentoTrabPartosPorId(gsoPacCodigo, gsoSeqp, matCodigo);
		McoMedicamentoTrabPartosId id = mcoMedicamentoTrabPartos.getId();
		
		McoMedicamentoTrabPartosJn jornal =  new McoMedicamentoTrabPartosJn();
		jornal.setCriadoEm(mcoMedicamentoTrabPartos.getCriadoEm());
		jornal.setDthrIni(mcoMedicamentoTrabPartos.getDataHoraInicial());
		jornal.setDthrFim(mcoMedicamentoTrabPartos.getDataHoraFinal());
		jornal.setGsoPacCodigo(mcoMedicamentoTrabPartos.getId().getGsoPacCodigo());
		jornal.setGsoSeqp(mcoMedicamentoTrabPartos.getId().getGsoSeqp());
		jornal.setMedMatCodigo(mcoMedicamentoTrabPartos.getId().getMedMatCodigo());
		jornal.setNomeUsuario(usuario.getLogin());
		jornal.setOperacao(DominioOperacoesJournal.DEL);
		jornal.setSerMatricula(usuario.getMatricula());
		jornal.setSerVinCodigo(usuario.getVinculo());
		jornal.setDose(mcoMedicamentoTrabPartos.getDose());
		mcoMedicamentoTrabPartosJnDAO.persistir(jornal);
		
		this.mcoMedicamentoTrabPartosDAO.excluirMedicamentoPorId(id);
	}

	public McoAtendTrabPartos buscarMcoAtendTrabPartos(Integer pacCodigo, Short seqp) {
		return this.mcoAtendTrabPartosDAO.obterMcoTrabPartosPorId(pacCodigo,seqp);
	}
	
	public McoTrabPartos buscarMcoTrabPartos(Integer pacCodigo, Short seqp) {
		return this.mcoTrabPartosDAO.obterMcoTrabPartosPorId(pacCodigo,seqp);
	}

	public RapServidorConselhoVO obterRapPessoasConselhoPorMatriculaVinculo(Short vinculo, Integer matricula) throws ApplicationBusinessException {
		RapServidorConselhoVO rapServidorConselhoVO = null;
		rapServidorConselhoVO = registroColaboradorService.obterServidorConselhoPeloId(null, matricula, vinculo);
		return rapServidorConselhoVO;
	}
	
	/**
	 * #25663 -  RN13
	 * @ORADB MCO_TRAB_PARTOS.MCOT_TPA_BRI 
	 * @param original
	 * @param dominio
	 * @return
	 */
	private boolean atualizaJornal(McoTrabPartos original, McoTrabPartos atendPartos){	
			if (CoreUtil.modificados(original.getDthriniCtg(), atendPartos.getDthriniCtg())
				||CoreUtil.modificados(original.getIndicacaoNascimento(), atendPartos.getIndicacaoNascimento())
				||CoreUtil.modificados(original.getIndicadorPartoInduzido(), atendPartos.getIndicadorPartoInduzido())
				||CoreUtil.modificados(original.getJustificativa(), atendPartos.getJustificativa())
				||CoreUtil.modificados(original.getObservacao(), atendPartos.getObservacao())
				||CoreUtil.modificados(original.getSerMatriculaIndicado(), atendPartos.getSerMatriculaIndicado())
				||CoreUtil.modificados(original.getSerVinCodigoIndicado(), atendPartos.getSerVinCodigoIndicado())
				||CoreUtil.modificados(original.getSerMatriculaIndicado2(), atendPartos.getSerMatriculaIndicado2())
				||CoreUtil.modificados(original.getSerVinCodigoIndicado2(), atendPartos.getSerVinCodigoIndicado2())
				||CoreUtil.modificados(original.getTipoParto(), atendPartos.getTipoParto())){
				
				return true;
			}
		return false;
	}
	
	private boolean atualizaJornalMedicamento(McoMedicamentoTrabPartos original, McoMedicamentoTrabPartos medicamentos){	
		if (CoreUtil.modificados(original.getDataHoraFinal(), medicamentos.getDataHoraFinal())
			||CoreUtil.modificados(original.getDataHoraInicial(), medicamentos.getDataHoraInicial())
			||CoreUtil.modificados(original.getObservacao(), medicamentos.getObservacao())
			||CoreUtil.modificados(original.getDose(), medicamentos.getDose())){
			
			return true;
		}
	return false;
}
	/**
	 * #25663 -  RN14
	 * @ORADB MCO_TRAB_PARTOS.MCOT_TPA_ARU 
	 * @param original
	 * @param dominio
	 * @return
	 */
	private void insereJornal(McoTrabPartos original, DominioOperacoesJournal operacao){
		McoTrabPartosJn  jn = BaseJournalFactory.getBaseJournal(operacao, McoTrabPartosJn.class,  usuario.getLogin()); 
		jn.setGsoPacCodigo(original.getId().getPacCodigo());
		jn.setGsoSeqp(original.getId().getSeqp());
		jn.setInaSeq(original.getIndicacaoNascimento() == null ? null : original.getIndicacaoNascimento().getSeq());
		jn.setDthriniCtg(original.getDthriniCtg());
		jn.setIndicacoesCtg(original.getIndicacoesCtg());
		jn.setJustificativa(original.getJustificativa());
		jn.setObservacao(original.getObservacao());
		jn.setSerMatriculaIndicado(original.getSerMatriculaIndicado());
		jn.setSerMatriculaIndicado2(original.getSerMatriculaIndicado2());
		jn.setSerVinCodigoIndicado(original.getSerVinCodigoIndicado());
		jn.setSerVinCodigoIndicado2(original.getSerVinCodigoIndicado2());
		jn.setTipoParto(original.getTipoParto() == null ? null : original.getTipoParto().toString() );
		this.mcoTrabPartosJnDAO.persistir(jn);
	}
	
	private void insereJornalMedicamentos(McoMedicamentoTrabPartos original, DominioOperacoesJournal operacao){
		McoMedicamentoTrabPartosJn  jornal = BaseJournalFactory.getBaseJournal(operacao, McoMedicamentoTrabPartosJn.class,  usuario.getLogin()); 
		jornal.setCriadoEm(original.getCriadoEm());
		jornal.setDthrIni(original.getDataHoraInicial());
		jornal.setDthrFim(original.getDataHoraFinal());
		jornal.setGsoPacCodigo(original.getId().getGsoPacCodigo());
		jornal.setGsoSeqp(original.getId().getGsoSeqp());
		jornal.setMedMatCodigo(original.getId().getMedMatCodigo());
		jornal.setNomeUsuario(usuario.getLogin());
		jornal.setOperacao(operacao);
		jornal.setSerMatricula(usuario.getMatricula());
		jornal.setSerVinCodigo(usuario.getVinculo());
		jornal.setDose(original.getDose());
		mcoMedicamentoTrabPartosJnDAO.persistir(jornal);
	}
	
	
	public void adicionarMedicamento(McoMedicamentoTrabPartos medicamento)
			throws ApplicationBusinessException {
		Integer gsoPacCodigo = medicamento.getId().getGsoPacCodigo();
		Short gsoSeqp = medicamento.getId().getGsoSeqp();
		Integer medMatCodigo = medicamento.getId().getMedMatCodigo();
		Date dataHoraInicial = medicamento.getDataHoraInicial();
		Date dataHoraFinal = medicamento.getDataHoraFinal();

		compararHoraMcoMedicamentosTrabPartos(dataHoraInicial, dataHoraFinal);

		verificarSeMcoMedicamentoTrabPartosExiste(gsoPacCodigo, gsoSeqp,
				medMatCodigo);

		medicamento.setCriadoEm(new Date());
		
		medicamento.setServidor(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
		this.mcoMedicamentoTrabPartosDAO.persistir(medicamento);
	}

	public void alterarMcoMedicamentoTrabPartos(
			McoMedicamentoTrabPartos medicamento)
			throws ApplicationBusinessException {
		Date dataHoraInicial = medicamento.getDataHoraInicial();
		Date dataHoraFinal = medicamento.getDataHoraFinal();

		compararHoraMcoMedicamentosTrabPartos(dataHoraInicial, dataHoraFinal);
		
		medicamento.setServidor(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
		
		McoMedicamentoTrabPartosId idOrigem = new McoMedicamentoTrabPartosId();
		idOrigem.setGsoPacCodigo(medicamento.getId().getGsoPacCodigo());
		idOrigem.setGsoSeqp(medicamento.getId().getGsoSeqp());
		idOrigem.setMedMatCodigo(medicamento.getId().getMedMatCodigo());
		McoMedicamentoTrabPartos origem = this.mcoMedicamentoTrabPartosDAO.obterOriginal(idOrigem);
		if (atualizaJornalMedicamento(origem, medicamento)) {
			insereJornalMedicamentos(origem, DominioOperacoesJournal.UPD);
		}
		this.mcoMedicamentoTrabPartosDAO.merge(medicamento);
	}

	private void verificarSeMcoMedicamentoTrabPartosExiste(
			Integer gsoPacCodigo, Short gsoSeqp, Integer medMatCodigo)
			throws ApplicationBusinessException {
		if (this.mcoMedicamentoTrabPartosDAO.existeMedicamento(gsoPacCodigo,
				gsoSeqp, medMatCodigo)) {
			throw new ApplicationBusinessException(
					RegistrarGestacaoRNExceptionCode.MENSAGEM_TRABALHO_PARTO_MCO_00087);
		}
	}

	/**
	 * 
	 * RN08 de #25663- REGISTRAR TRABALHO DE PARTO
	 * 
	 * @ORADB MCO_MEDICAMENTO_TRAB_PARTOS.MCOT_MTR_BRI – trigger before insert
	 * 
	 * 
	 * @param dataHoraInicial
	 * @param dataHoraFinal
	 */
	private void compararHoraMcoMedicamentosTrabPartos(Date dataHoraInicial,
			Date dataHoraFinal) throws ApplicationBusinessException {
		if (dataHoraInicial != null && DateUtil.validaDataMenor(dataHoraFinal, dataHoraInicial)) {
			throw new ApplicationBusinessException(
					RegistrarGestacaoRNExceptionCode.MENSAGEM_TRABALHO_PARTO_MCO_00516);
		}
	}
	
	// #25123 - RN05 - Passo 6 (a)
	public void validarDadosConsultaCO(McoBolsaRotas bolsaRotas) throws ApplicationBusinessException{
		
		// RN04 - #25663
		if(bolsaRotas.getId() == null) {
			this.validarDadosBolsaRotas(bolsaRotas);
		}
		// RN05 - #25663
		else {
			McoBolsaRotas original = this.bolsaRotasDAO.obterOriginal(bolsaRotas);
			this.atualizarDadosBolsaRotas(bolsaRotas);
			this.insereMcoBolsaRotasJn(original);
		}
	}

	// #25123 - RN05 - Passo 6 (b)
	public void validarDadosBolsaRotas(McoBolsaRotas bolsaRotas) throws ApplicationBusinessException{
		
		// RN03 - #25663
		this.validarMcoBolsaRotas(bolsaRotas.getDthrRompimento(), bolsaRotas.getDominioFormaRuptura());
		this.bolsaRotasDAO.persistir(bolsaRotas);
	}

	public void atualizarDadosBolsaRotas(McoBolsaRotas bolsaRotas) throws ApplicationBusinessException{
		this.validarMcoBolsaRotas(bolsaRotas.getDthrRompimento(), bolsaRotas.getDominioFormaRuptura());
		this.bolsaRotasDAO.merge(bolsaRotas);
	}

	public List<McoAtendTrabPartos> buscarListaMcoAtendTrabPartos(
			Integer pacCodigo, Short seqp) {
		
		return this.mcoAtendTrabPartosDAO.buscarListaMcoAtendTrabPartos(pacCodigo,seqp);
	}

}