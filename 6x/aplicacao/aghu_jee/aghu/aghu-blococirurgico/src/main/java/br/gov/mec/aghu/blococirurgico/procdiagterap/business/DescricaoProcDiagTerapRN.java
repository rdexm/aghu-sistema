package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaAnestesiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaEquipeAnestesiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaTipoAnestesiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtCidDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDadoDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescObjetivaDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescTecnicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtInstrDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtMedicDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtNotaAdicionalDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtProcDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtProcDiagTerapDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtProfDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtSolicTempDAO;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.PdtCidDescON.PdtCidDescONExceptionCode;
import br.gov.mec.aghu.blococirurgico.vo.CertificarRelatorioCirurgiasPdtVO;
import br.gov.mec.aghu.blococirurgico.vo.ProcEspPorCirurgiaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioCaraterCirurgia;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricao;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaTipoAnestesia;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.model.PdtDadoDesc;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.PdtProc;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.model.PdtProcId;
import br.gov.mec.aghu.model.PdtProf;
import br.gov.mec.aghu.model.PdtProfId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável pelas regras de negócio relacionadas a Descrição de PDT
 * (carga da descrição pendente).
 * 
 * @author dpacheco
 * 
 */
@SuppressWarnings({ "PMD.CouplingBetweenObjects"})
@Stateless
public class DescricaoProcDiagTerapRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(DescricaoProcDiagTerapRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtSolicTempDAO pdtSolicTempDAO;

	@Inject
	private PdtMedicDescDAO pdtMedicDescDAO;

	@Inject
	private PdtDescricaoDAO pdtDescricaoDAO;

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcFichaAnestesiasDAO mbcFichaAnestesiasDAO;

	@Inject
	private MbcAnestesiaCirurgiasDAO mbcAnestesiaCirurgiasDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private PdtDescObjetivaDAO pdtDescObjetivaDAO;

	@Inject
	private MbcFichaTipoAnestesiaDAO mbcFichaTipoAnestesiaDAO;

	@Inject
	private PdtDadoDescDAO pdtDadoDescDAO;

	@Inject
	private PdtProcDiagTerapDAO pdtProcDiagTerapDAO;

	@Inject
	private PdtProcDAO pdtProcDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private PdtInstrDescDAO pdtInstrDescDAO;

	@Inject
	private PdtProfDAO pdtProfDAO;

	@Inject
	private PdtDescTecnicaDAO pdtDescTecnicaDAO;

	@Inject
	private PdtCidDescDAO pdtCidDescDAO;

	@Inject
	private MbcFichaEquipeAnestesiaDAO mbcFichaEquipeAnestesiaDAO;

	@Inject
	private PdtNotaAdicionalDAO pdtNotaAdicionalDAO;


	@EJB
	private PdtNotaAdicionalRN pdtNotaAdicionalRN;

	@EJB
	private ICascaFacade iCascaFacade;

	@EJB
	private PdtProcRN pdtProcRN;

	@EJB
	private PdtDescTecnicaRN pdtDescTecnicaRN;

	@EJB
	private PdtMedicDescRN pdtMedicDescRN;

	@EJB
	private LiberacaoLaudoPreliminarON liberacaoLaudoPreliminarON;

	@EJB
	private PdtDadoDescRN pdtDadoDescRN;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private PdtDescricaoRN pdtDescricaoRN;

	@EJB
	private PdtDescObjetivaRN pdtDescObjetivaRN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private PdtCidDescRN pdtCidDescRN;

	@EJB
	private PdtProfRN pdtProfRN;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	@EJB
	private ICertificacaoDigitalFacade iCertificacaoDigitalFacade;

	@EJB
	private PdtInstrDescRN pdtInstrDescRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2347287159044630146L;
	
	private static final String PERMISSAO_ASSINATURA_DIGITAL = "assinaturaDigital";
	
	public enum DescricaoProcDiagTerapONExceptionCode implements BusinessExceptionCode {
		PDT_00121, PDT_00123, MBC_00696, MBC_00697, MBC_01802
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: PDTP_CARRG_DESCRICAO
	 * 
	 * @param crgSeq
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public PdtDescricao carregarDescricaoProcDiagTerap(Integer crgSeq) throws ApplicationBusinessException {
		AghEspecialidades especialidade = null;
		DominioCaraterCirurgia carater = null;
		Date dthrInicioCirurgia = null;
		Date dthrFimCirurgia = null;
		Boolean digitaNotaSala = null;
		DominioSituacaoCirurgia situacaoCirurgia = null;
		AghUnidadesFuncionais unidadeFuncional = null;
		MbcFichaAnestesias fichaAnestesia = null;
		MbcTipoAnestesias tipoAnestesia = null;
		
		//c_cirurgia
		MbcCirurgias cirurgia = getMbcCirurgiasDAO().obterPorChavePrimaria(crgSeq);
		
		if (cirurgia != null) {
			especialidade = cirurgia.getEspecialidade();
			
			if (DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda())) {
				carater = DominioCaraterCirurgia.ELT;
			} else {
				carater = DominioCaraterCirurgia.URG;
			}
			
			dthrInicioCirurgia = cirurgia.getDataInicioCirurgia();
			dthrFimCirurgia = cirurgia.getDataFimCirurgia();
			digitaNotaSala = cirurgia.getDigitaNotaSala();
			situacaoCirurgia = cirurgia.getSituacao();
			unidadeFuncional = cirurgia.getUnidadeFuncional();
		}
		
		PdtDescricao descricao = new PdtDescricao();
		descricao.setSituacao(DominioSituacaoDescricao.PEN);
		descricao.setDthrConclusao(null);
		descricao.setDthrExecucao(null);
		descricao.setComplemento(null);
		descricao.setMbcCirurgias(cirurgia);
		descricao.setItemSolicitacaoExame(null);
		descricao.setEspecialidade(especialidade);
		descricao.setCriadoEm(null);
		descricao.setServidor(null);
		descricao.setResultadoNormal(Boolean.FALSE);
		
		try {
			/* Inclui pdt _descricoes */
			getPdtDescricaoRN().inserirDescricao(descricao);
		} catch (ApplicationBusinessException e) {
			logError(e);
			throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.PDT_00121, e.getLocalizedMessage());
		} catch (PersistenceException e) {
			logError(e);
			throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.PDT_00121, e.getLocalizedMessage());
		}
		
		Integer ddtSeq = descricao.getSeq();
		
		// Verifica se já existe ficha de anestesia para esta cirurgia
		List<MbcFichaAnestesias> listaFichaAnestesia = getMbcFichaAnestesiasDAO().listarFichasAnestesiasPorCirurgias(crgSeq);
		if (!listaFichaAnestesia.isEmpty()) {
			fichaAnestesia = listaFichaAnestesia.get(0);
			// Verifica se na ficha já tem a informação do tipo de anestesia
			List<MbcFichaTipoAnestesia> listaFichaTipoAnestesia = getMbcFichaTipoAnestesiaDAO().pesquisarMbcFichasTipoAnestesias(fichaAnestesia.getSeq());
			if (!listaFichaTipoAnestesia.isEmpty()) {
				tipoAnestesia = listaFichaTipoAnestesia.get(0).getTipoAnestesia();
			}
		}
		
		// Se não conseguiu buscar o tipo de anestesia da ficha, busca do agendamento
		if (tipoAnestesia == null) {
			List<MbcAnestesiaCirurgias> listaAnestesiaCirurgia = getMbcAnestesiaCirurgiasDAO().listarAnestesiaCirurgiaTipoAnestesiaPorCrgSeq(crgSeq);
			if (!listaAnestesiaCirurgia.isEmpty()) {
				tipoAnestesia = listaAnestesiaCirurgia.get(0).getMbcTipoAnestesias();	
			}
		}
		
		PdtDadoDesc dadoDesc = new PdtDadoDesc();
		dadoDesc.setDdtSeq(ddtSeq);
		dadoDesc.setPdtDescricao(descricao);
		dadoDesc.setSedacao(Boolean.FALSE);
		dadoDesc.setNroFilme(null);
		dadoDesc.setDthrInicio(dthrInicioCirurgia);
		dadoDesc.setDthrFim(dthrFimCirurgia);
		dadoDesc.setCarater(carater);
		dadoDesc.setObservacoesProc(null);
		dadoDesc.setMbcTipoAnestesias(tipoAnestesia);
		dadoDesc.setPdtTecnica(null);
		dadoDesc.setPdtEquipamento(null);
		dadoDesc.setCriadoEm(null);
		dadoDesc.setRapServidores(null);
		
		try {
			/* Inclui pdt_dados_descs */
			getPdtDadoDescRN().inserirDadoDesc(dadoDesc);
		} catch (ApplicationBusinessException e) {
			logError(e);
			throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.PDT_00123, e.getLocalizedMessage());
		} catch (PersistenceException e) {
			logError(e);
			throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.PDT_00123, e.getLocalizedMessage());
		}
		
		inserirProfissionais(crgSeq, servidorLogadoFacade.obterServidorLogado(), descricao, ddtSeq, fichaAnestesia, unidadeFuncional);
		
		inserirProcedimentos(ddtSeq, crgSeq, descricao, situacaoCirurgia, digitaNotaSala);
		
		return descricao;
	}

	private void inserirProfissionais(
			Integer crgSeq,
			RapServidores servidorLogado,
			PdtDescricao descricao, Integer ddtSeq, MbcFichaAnestesias fichaAnestesia, AghUnidadesFuncionais unidadeFuncional)
			throws ApplicationBusinessException {
		
		Short dpfSeqp;
		
		// Acha a proxima seqp de pdt_profs
		Short dpfSeqpAtual = getPdtProfDAO().obterMaiorSeqpProfPorDdtSeq(ddtSeq);
		
		if (dpfSeqpAtual != null) {
			dpfSeqp = (short) (dpfSeqpAtual + 1);
		} else {
			dpfSeqp = 1;	
		}
		
		PdtProfRN profRN = getPdtProfRN();
		
		// Inclui em pdt_profs
		List<MbcProfCirurgias> listaProfCirurgias = getMbcProfCirurgiasDAO().pesquisarProfCirurgiasPorCrgSeq(crgSeq);
		
		if (!listaProfCirurgias.isEmpty()) {

			final DominioFuncaoProfissional[] arrayFuncaoProfissional = {
					DominioFuncaoProfissional.MPF,
					DominioFuncaoProfissional.MCO,
					DominioFuncaoProfissional.MAX };

			final DominioFuncaoProfissional[] arrayFuncaoProfissionalEnf = {
					DominioFuncaoProfissional.ENF,
					DominioFuncaoProfissional.INS,
					DominioFuncaoProfissional.CIR };

			final DominioFuncaoProfissional[] arrayFuncaoProfissionalAnes = {
					DominioFuncaoProfissional.ANP,
					DominioFuncaoProfissional.ANR,
					DominioFuncaoProfissional.ANC };
			
			PdtProf prof = new PdtProf();
			prof.setPdtDescricao(descricao);

			for (MbcProfCirurgias profCirurgia : listaProfCirurgias) {

				try {

					if (profCirurgia.getIndResponsavel()) {

						profRN.inserirProf(
								popularPdtProf(prof, new PdtProfId(ddtSeq,dpfSeqp), DominioTipoAtuacao.RESP, profCirurgia.getServidorPuc()));
						dpfSeqp++;

						profRN.inserirProf(
								popularPdtProf(prof, new PdtProfId(ddtSeq,dpfSeqp), DominioTipoAtuacao.CIRG, profCirurgia.getServidorPuc()));
						dpfSeqp++;

					} else {

						if (!profCirurgia.getIndRealizou()
								&& isDominioFuncaoProfissionalValidado(profCirurgia.getFuncaoProfissional(), arrayFuncaoProfissional)) {
			
							profRN.inserirProf(
									popularPdtProf(prof, new PdtProfId(ddtSeq, dpfSeqp), DominioTipoAtuacao.AUX, profCirurgia.getServidorPuc()));
							dpfSeqp++;
						}
					}

					if (isDominioFuncaoProfissionalValidado(profCirurgia.getFuncaoProfissional(), arrayFuncaoProfissionalEnf)) {
						
						profRN.inserirProf(
								popularPdtProf(prof, new PdtProfId(ddtSeq, dpfSeqp), DominioTipoAtuacao.ENF, profCirurgia.getServidorPuc()));
						dpfSeqp++;
					}
					
				} catch (PersistenceException e) {
					logError(e);
					throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.MBC_00696, e);
					
				} catch (ApplicationBusinessException e) {
					logError(e);
					throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.MBC_00696, e);
				}

				if (fichaAnestesia == null
						&& isDominioFuncaoProfissionalValidado(profCirurgia.getFuncaoProfissional(), arrayFuncaoProfissionalAnes)) {
					
					try {

						profRN.inserirProf(
								popularPdtProf(prof, new PdtProfId(ddtSeq, dpfSeqp), DominioTipoAtuacao.ANES, profCirurgia.getServidorPuc()));
						dpfSeqp++;

					} catch (ApplicationBusinessException e) {
						logError(e);
						throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.MBC_01802, e.getLocalizedMessage());

					} catch (PersistenceException e) {
						logError(e);
						throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.MBC_01802, e.getLocalizedMessage());
					}

				}
			}

		}
		
		inserirProfissionaisAnestesistas(servidorLogado, descricao, ddtSeq,
				fichaAnestesia, unidadeFuncional, dpfSeqp, profRN);
	}
	
	private PdtProf popularPdtProf(PdtProf prof, PdtProfId pdtProfId,
			DominioTipoAtuacao dominioTipoAtuacao, RapServidores rapServidores) throws ApplicationBusinessException
			{

		PdtProf profTipoAtuacao = null;
		
		try {
			profTipoAtuacao = (PdtProf) BeanUtils.cloneBean(prof);
			profTipoAtuacao.setId(pdtProfId);
			profTipoAtuacao.setTipoAtuacao(dominioTipoAtuacao);
			profTipoAtuacao.setServidorPrf(rapServidores);
			
		} catch (IllegalAccessException e) {
			logError(e);
			throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.MBC_00696, e);

		} catch (InstantiationException e) {
			logError(e);
			throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.MBC_00696, e);

		} catch (InvocationTargetException e) {
			logError(e);
			throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.MBC_00696, e);

		} catch (NoSuchMethodException e) {
			logError(e);
			throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.MBC_00696, e);
		}


		return profTipoAtuacao;
	}

	private boolean isDominioFuncaoProfissionalValidado(final DominioFuncaoProfissional funcaoDoProfissional, final DominioFuncaoProfissional... dominioFuncaoProfissional) {

		if (Arrays.binarySearch(dominioFuncaoProfissional, funcaoDoProfissional) >= 0) {
			return true;
		} else {
			return false;
		}
	}

	private void inserirProfissionaisAnestesistas(RapServidores servidorLogado,
			PdtDescricao descricao, Integer ddtSeq,
			MbcFichaAnestesias fichaAnestesia,
			AghUnidadesFuncionais unidadeFuncional, Short dpfSeqp, PdtProfRN profRN)
			throws ApplicationBusinessException {
		IRegistroColaboradorFacade registroColaboradorFacade = getRegistroColaboradorFacade();
		
		// Se tem ficha, carrega os profissionais de anestesia da ficha
		if (fichaAnestesia != null) {
			List<RapServidoresVO> listaFichaEquipeAnestesia = getMbcFichaEquipeAnestesiaDAO()
					.pesquisarFichaAnestesiasAssociadaProfAtuaUnidCirg(fichaAnestesia.getSeq(),
							unidadeFuncional.getSeq());
			
			// Inclui os anestesistas em pdt_prof
			for (RapServidoresVO servidorVO : listaFichaEquipeAnestesia) {
				PdtProf prof = new PdtProf();
				prof.setId(new PdtProfId(ddtSeq, dpfSeqp));
				prof.setNome(null);
				prof.setCategoria(null);
				prof.setCriadoEm(null);
				prof.setServidor(null);
				prof.setPdtDescricao(descricao);
				
				RapServidores servidorProf = registroColaboradorFacade.obterServidor(
						new RapServidoresId(servidorVO.getMatricula(), servidorVO.getVinculo()));
				
				prof.setTipoAtuacao(DominioTipoAtuacao.ANES);
				prof.setServidorPrf(servidorProf);
				
				try {
					profRN.inserirProf(prof);
					dpfSeqp++;
				} catch (ApplicationBusinessException e) {
					logError(e);
					throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.MBC_01802, e.getLocalizedMessage());
				} catch (PersistenceException e) {
					logError(e);
					throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.MBC_01802, e.getLocalizedMessage());					
				}
			}			
		}
	}
	
	private void inserirProcedimentos(Integer ddtSeq, Integer crgSeq, PdtDescricao descricao, DominioSituacaoCirurgia situacaoCirurgia, 
			Boolean digitaNotaSala) throws ApplicationBusinessException {
		Short dicSeqp = null;
		
		/* Acha a proxima seqp pdt_procs*/
		Short dicSeqpAtual = getPdtProcDAO().obterMaiorSeqpProcPorDdtSeq(ddtSeq);
		if (dicSeqpAtual != null) {
			dicSeqp = (short) (dicSeqpAtual + 1);			
		} else {
			dicSeqp = 1;
		}
		
		final PdtProcRN pdtProcRN = getPdtProcRN(); 
		if (DominioSituacaoCirurgia.RZDA.equals(situacaoCirurgia) && Boolean.TRUE.equals(digitaNotaSala)) {
			
			/* Inclui pdt_procs a partir dos procedimentos realizados */
			List<ProcEspPorCirurgiaVO> listaProcEspCrgRealizada = 
					getMbcProcEspPorCirurgiasDAO().pesquisarProcEspCirurgiaRealizadaPorCrgSeqEIndRespProc(crgSeq, DominioIndRespProc.NOTA);
			
			for (ProcEspPorCirurgiaVO procEspPorCirurgiaVO : listaProcEspCrgRealizada) {
				PdtProc proc = new PdtProc();
				proc.setId(new PdtProcId(ddtSeq, dicSeqp));
				proc.setPdtDescricao(descricao);
				proc.setIndContaminacao(procEspPorCirurgiaVO.getIndContaminacao());
				proc.setComplemento(null);
				
				PdtProcDiagTerap procDiagTerap = getPdtProcDiagTerapDAO().obterPorChavePrimaria(procEspPorCirurgiaVO.getDptSeq());
				proc.setPdtProcDiagTerap(procDiagTerap);
				
				proc.setCriadoEm(null);
				proc.setRapServidores(null);
				
				try {
					pdtProcRN.inserirProc(proc);
					dicSeqp++;
				} catch (ApplicationBusinessException e) {
					logError(e);
					throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.MBC_00697);
				} catch (PersistenceException e) {
					logError(e);
					throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.MBC_00697);
				}
			}
		} else {
			/* Inclui pdt_procs a partir dos procedimentos agendados */
			List<ProcEspPorCirurgiaVO> listaProcEspCrgAgendado = 
					getMbcProcEspPorCirurgiasDAO().pesquisarProcEspCirurgiaRealizadaPorCrgSeqEIndRespProc(crgSeq, DominioIndRespProc.AGND);
			for (ProcEspPorCirurgiaVO procEspPorCirurgiaVO : listaProcEspCrgAgendado) {
				PdtProc proc = new PdtProc();
				proc.setId(new PdtProcId(ddtSeq, dicSeqp));
				proc.setPdtDescricao(descricao);
				proc.setIndContaminacao(procEspPorCirurgiaVO.getIndContaminacao());
				proc.setComplemento(null);
				
				PdtProcDiagTerap procDiagTerap = getPdtProcDiagTerapDAO().obterPorChavePrimaria(procEspPorCirurgiaVO.getDptSeq());
				proc.setPdtProcDiagTerap(procDiagTerap);
				
				proc.setCriadoEm(null);
				proc.setRapServidores(null);
				
				try {
					pdtProcRN.inserirProc(proc);
					dicSeqp++;
				} catch (ApplicationBusinessException e) {
					logError(e);
					throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.MBC_00697);
				} catch (PersistenceException e) {
					logError(e);
					throw new ApplicationBusinessException(DescricaoProcDiagTerapONExceptionCode.MBC_00697);
				}
			}			
		}
	} 
	
	/**
	 * 
	 * Valor padrão de retorno: FALSE, desativando a Aba Notas Adicionais e ativando todo o resto 
	 *
	 * ORADB: PDTF_DESCRICAO.EVT_POST_QUERY
	 * 
	 * @author eSchweigert
	 *  
	 */
	public boolean habilitarNotasAdicionais(final Integer crgSeq, final PdtDescricao descricao) throws ApplicationBusinessException {
		if(CoreUtil.igual(descricao.getServidor(), servidorLogadoFacade.obterServidorLogado())){
			
			if(descricao.getDthrConclusao() != null){
				final Integer vMinutos = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_MIN_ALTERA_DESCR_PDT);
				final int vLimite = DateUtil.obterQtdMinutosEntreDuasDatas(descricao.getDthrConclusao(), new Date()); 
				//  v_limite:= trunc(((SYSDATE - fet_ddt.dthr_conclusao) *24 )  *60);
				
				// proteger tudo menos as Notas Adcicionais
				if(vLimite > vMinutos){
					// habilitar NDA
					if(DominioSituacaoDescricao.DEF.equals(descricao.getSituacao())){
						//  k_variaveis.v_usuario_autorizado:=false;
						return true;
						
					// pode fazer qquer coisa
					} else if(DominioSituacaoDescricao.PRE.equals(descricao.getSituacao()) ||
							DominioSituacaoDescricao.PEN.equals(descricao.getSituacao())){
						
						//  k_variaveis.v_usuario_autorizado:=true;
						return false;
					}  
						
					return true;
				
				} else { // dentro do limite pode tudo. Protege NDA
					// k_variaveis.v_usuario_autorizado:=true;
					return false;
				}
				
			
			// elsif fet_ddt.situacao = 'PRE' OR fet_ddt.situacao = 'PEN' THEN --faz qqer coisa
			} else if(DominioSituacaoDescricao.PRE.equals(descricao.getSituacao()) ||
					DominioSituacaoDescricao.PEN.equals(descricao.getSituacao())){	
				//  k_variaveis.v_usuario_autorizado:=true;
				
				return false;
			}

			return false;
		} else { // usr q não incluiu a descr
			// k_variaveis.v_usuario_autorizado := false;
			return true;
		}
	}
	
	public CertificarRelatorioCirurgiasPdtVO pGeraCertif(final Integer crgSeq, final Short unfSeq, final DominioTipoDocumento tipoDocumento) throws BaseException {
		final ICertificacaoDigitalFacade certificacaoDigitalFacade = getCertificacaoDigitalFacade();
		
		// Integração com a Certificação Digital
		// Verifica se está habilitada para uso de certificação digital
		
		// v_habilita_certif := aghk_certif_digital.aghc_habilita_certif('PDTF_DESCRICAO');
		final boolean vHabilitaCertif = certificacaoDigitalFacade.verificaAssituraDigitalHabilitada();
		
		// Verifica se o usuário tem permissão para assinatura digital de documentos AGH RN002
		final Boolean vAssinaturaDigital = this.getICascaFacade().usuarioTemPermissao(servidorLogadoFacade.obterServidorLogado().getUsuario(), PERMISSAO_ASSINATURA_DIGITAL);
		
		Boolean previa = true;
		Integer nroCopias;
		
		if(vHabilitaCertif && Boolean.TRUE.equals(vAssinaturaDigital)){
			if(DominioTipoDocumento.PRE.equals(tipoDocumento)) {
				if(getAghuFacade().validarCaracteristicaDaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.HEMODINAMICA)) {
					previa = false;
				}
			}
			return new CertificarRelatorioCirurgiasPdtVO(crgSeq, null, previa, true, false);
		}
		else {
			if(DominioTipoDocumento.PRE.equals(tipoDocumento)) {
				if(getAghuFacade().validarCaracteristicaDaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.HEMODINAMICA)) {
					previa = false;
					nroCopias = 2;
				}
				else {
					nroCopias = 1;
				}
				return new CertificarRelatorioCirurgiasPdtVO(crgSeq, nroCopias, previa, false, true);
				//PDTP_IMPRIME_LAUDO;
			}
			else {
				return new CertificarRelatorioCirurgiasPdtVO(crgSeq, 2, true, false, true);
				//PDTP_IMPRIME_LAUDO2;
			}
		}
	}

	public CertificarRelatorioCirurgiasPdtVO liberarLaudoDefinitivo(final Integer ddtSeq, final Integer crgSeq, final Short unfSeq, final Date dtExecucao, final DominioTipoDocumento tipoDocumento) 
			throws BaseException {
		PdtDescricao descricao = getPdtDescricaoDAO().obterPorChavePrimaria(ddtSeq);
		PdtDadoDesc dadoDesc = getPdtDadoDescDAO().obterPorChavePrimaria(ddtSeq);

		MbcCirurgias cirurgia = descricao.getMbcCirurgias();
		
		MbcSalaCirurgica salaCirurgica = null;
		
		if (cirurgia != null) {
			salaCirurgica = cirurgia.getSalaCirurgica();
		}
		
		//******************
		//****VALIDAÇÕES****
		//******************
		if((descricao.getCidsDesc() == null || descricao.getCidsDesc().isEmpty()) && Boolean.FALSE.equals(descricao.getResultadoNormal())) {
			throw new ApplicationBusinessException(PdtCidDescONExceptionCode.PDT_00173);
		}
			
		getLiberacaoLaudoPreliminarON().verificarColisao(unfSeq, cirurgia.getData(), crgSeq, salaCirurgica != null ? salaCirurgica.getId().getUnfSeq() : null, 
				salaCirurgica != null ? salaCirurgica.getId().getSeqp() : null, dadoDesc.getDthrInicio(), dadoDesc.getDthrFim());
		
		if(dtExecucao == null) {
			descricao.setSituacao(DominioSituacaoDescricao.DEF);
			descricao.setDthrConclusao(new Date());
			descricao.setDthrExecucao(new Date());
			getPdtDescricaoRN().atualizarDescricao(descricao, false);
		}
		else {
			descricao.setSituacao(DominioSituacaoDescricao.DEF);
			descricao.setDthrConclusao(new Date());
			getPdtDescricaoRN().atualizarDescricao(descricao, false);
		}
		getBlocoCirurgicoFacade().desbloqDocCertificacao(crgSeq, DominioTipoDocumento.PDT);
		return this.pGeraCertif(crgSeq, unfSeq, tipoDocumento);
	}
	
	private LiberacaoLaudoPreliminarON getLiberacaoLaudoPreliminarON() {
		return liberacaoLaudoPreliminarON;
	}

	protected PdtInstrDescRN getPdtInstrDescRN() {
		return pdtInstrDescRN;
	}

	protected PdtMedicDescRN getPdtMedicDescRN() {
		return pdtMedicDescRN;
	}

	protected PdtDescTecnicaRN getPdtDescTecnicaRN() {
		return pdtDescTecnicaRN;
	}

	protected PdtProcRN getPdtProcRN() {
		return pdtProcRN;
	}
	
	protected PdtDescricaoRN getPdtDescricaoRN() {
		return pdtDescricaoRN;
	}
	
	protected PdtDadoDescRN getPdtDadoDescRN() {
		return pdtDadoDescRN;
	}
	
	protected PdtProfRN getPdtProfRN() {
		return pdtProfRN;
	}
	
	protected PdtNotaAdicionalRN getPdtNotaAdicionalRN() {
		return pdtNotaAdicionalRN;
	}

	protected PdtCidDescRN getPdtCidDescRN() {
		return pdtCidDescRN;
	}

	protected PdtDescObjetivaRN getPdtDescObjetivaRN() {
		return pdtDescObjetivaRN;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return iBlocoCirurgicoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}	

	protected ICertificacaoDigitalFacade getCertificacaoDigitalFacade() {
		return this.iCertificacaoDigitalFacade;
	}	
	
	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}	
	
	protected PdtProfDAO getPdtProfDAO() {
		return pdtProfDAO;
	}

	protected PdtProcDAO getPdtProcDAO() {
		return pdtProcDAO;
	}

	protected PdtProcDiagTerapDAO getPdtProcDiagTerapDAO() {
		return pdtProcDiagTerapDAO;
	}

	protected PdtDescObjetivaDAO getPdtDescObjetivaDAO() {
		return pdtDescObjetivaDAO;
	}

	protected PdtDadoDescDAO getPdtDadoDescDAO() {
		return pdtDadoDescDAO;
	}

	protected PdtCidDescDAO getPdtCidDescDAO() {
		return pdtCidDescDAO;
	}

	protected PdtNotaAdicionalDAO getPdtNotaAdicionalDAO() {
		return pdtNotaAdicionalDAO;
	}

	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected MbcFichaAnestesiasDAO getMbcFichaAnestesiasDAO() {
		return mbcFichaAnestesiasDAO;
	}
	
	protected MbcFichaTipoAnestesiaDAO getMbcFichaTipoAnestesiaDAO() {
		return mbcFichaTipoAnestesiaDAO;
	}
	
	protected MbcAnestesiaCirurgiasDAO getMbcAnestesiaCirurgiasDAO() {
		return mbcAnestesiaCirurgiasDAO;
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}
	
	protected MbcFichaEquipeAnestesiaDAO getMbcFichaEquipeAnestesiaDAO() {
		return mbcFichaEquipeAnestesiaDAO;
	}
	
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}

	protected PdtDescricaoDAO getPdtDescricaoDAO() {
		return pdtDescricaoDAO;
	}

	protected PdtSolicTempDAO getPdtSolicTempDAO() {
		return pdtSolicTempDAO;
	}

	protected PdtDescTecnicaDAO getPdtDescTecnicaDAO() {
		return pdtDescTecnicaDAO;
	}

	protected PdtMedicDescDAO getPdtMedicDescDAO() {
		return pdtMedicDescDAO;
	}

	protected PdtInstrDescDAO getPdtInstrDescDAO() {
		return pdtInstrDescDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected ICascaFacade getICascaFacade() {
		return this.iCascaFacade;
	}	
}