package br.gov.mec.aghu.blococirurgico.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.vo.NomeArquivoRelatorioCrgVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiasPendenteRetornoVO;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoPendenciaCirurgia;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RelatorioCirurgiasPendenteRetornoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioCirurgiasPendenteRetornoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtDescricaoDAO pdtDescricaoDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcDescricaoCirurgicaDAO mbcDescricaoCirurgicaDAO;


	@EJB
	private IProntuarioOnlineFacade iProntuarioOnlineFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade iBlocoCirurgicoPortalPlanejamentoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1546832387594469516L;
	
	private static final String DATE_PATTERN_DD_MM_YY = "dd/MM/yy";
	private static final String LOCALE_LANG = "pt";
	private static final String LOCALE_COUNTRY = "BR";
	private static final String ENCODING = "ISO-8859-1";
	private static final String SEPARADOR = ";";	
	private static final String QUEBRA_LINHA = "\n";
	
	public enum RelatorioCirurgiasPendenteRetornoONExceptionCode implements BusinessExceptionCode {
		ERRO_RELATORIO_CIRURGIAS_PENDENTE_RETORNO_TIPO_DE_PENDENCIA_NAO_INFORMADO
	}
	
	public List<RelatorioCirurgiasPendenteRetornoVO> pesquisarCirurgiasPendenteRetorno(
			DominioTipoPendenciaCirurgia tipoPendenciaCirurgia, Short unfSeq, 
			Date dtInicio, Date dtFim, Integer pciSeq) throws ApplicationBusinessException {
		
		List<RelatorioCirurgiasPendenteRetornoVO> lista = new ArrayList<RelatorioCirurgiasPendenteRetornoVO>();
		
		AghParametros paramGrMatOrtProt = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_GR_MAT_ORT_PROT);
		Integer codGrMatOrtProt = paramGrMatOrtProt.getVlrNumerico().intValue();
		
		if (DominioTipoPendenciaCirurgia.PRODUCAO.equals(tipoPendenciaCirurgia) || 
				DominioTipoPendenciaCirurgia.DESC_CIR_PDT.equals(tipoPendenciaCirurgia)) {
			
			if (pciSeq == null) {
				lista.addAll(getMbcCirurgiasDAO().pesquisarCirurgiaNaoCanceladaPorUnfSeqDtInicioDtFim(unfSeq, dtInicio, dtFim));	
			}
			lista.addAll(getMbcCirurgiasDAO().pesquisarCirurgiaNaoCanceladaProcedPrincipalPorUnfSeqDtInicioDtFimEPciSeq(unfSeq, dtInicio, dtFim, pciSeq));
			
			// Uso de HashSet para fazer o distinct do UNION aplicado na lista
			Set<RelatorioCirurgiasPendenteRetornoVO> setCirurgiasPendenteRetornoVO = 
					new HashSet<RelatorioCirurgiasPendenteRetornoVO>(lista);
			lista = new ArrayList<RelatorioCirurgiasPendenteRetornoVO>(setCirurgiasPendenteRetornoVO);
			
			ordenarLista(lista);
			
		} else if (DominioTipoPendenciaCirurgia.NOTA_CONSUMO.equals(tipoPendenciaCirurgia)) {
			
			lista = getMbcCirurgiasDAO().pesquisarCirurgiaNaoCanceladaDigitaNotaSalaPorUnfSeqDtInicioDtFim(
					unfSeq, dtInicio, dtFim, codGrMatOrtProt);
		}
		
		for (Iterator<RelatorioCirurgiasPendenteRetornoVO> iterator = lista.iterator(); iterator.hasNext();) {
			RelatorioCirurgiasPendenteRetornoVO vo = (RelatorioCirurgiasPendenteRetornoVO) iterator.next();
			
			List<MbcDescricaoCirurgica> listaDescricaoCir = getMbcDescricaoCirurgicaDAO()
					.listarDescricaoCirurgicaPorSeqCirurgiaSituacao(vo.getCrgSeq(), DominioSituacaoDescricaoCirurgia.CON);
			
			List<PdtDescricao> listaDescricaoPdt = getPdtDescricaoDAO()
					.listarDescricaoPorCrgSeqESituacao(vo.getCrgSeq(), DominioSituacaoDescricao.DEF);
			
			// Possui alguma descrição (cirurgica ou PDT)
			if (!listaDescricaoCir.isEmpty() || !listaDescricaoPdt.isEmpty()) {
				vo.setDc(DominioSimNao.S.toString());
			} else if (listaDescricaoCir.isEmpty() && listaDescricaoPdt.isEmpty()) {
				vo.setDc(DominioSimNao.N.toString());
			}
						
			if (DominioTipoPendenciaCirurgia.DESC_CIR_PDT.equals(tipoPendenciaCirurgia) &&
					vo.getDc().equals(DominioSimNao.N.toString())) {
				iterator.remove();
				continue;
			}
			
			String nomeUsual = vo.getPesNomeUsual();
			String nomePessoa = vo.getPesNome();
			String equipe = "";
			if (nomeUsual == null) {
				if(nomePessoa.length() < 15){
					equipe = nomePessoa;
				}else{
					equipe = nomePessoa.substring(0, 15);
				}	
			} else {
				equipe = nomeUsual;
			}
			
			vo.setOrigem(vo.getOrigemPacCirg().name());

			vo.setStrProntuario(CoreUtil.formataProntuario(vo.getProntuario()));
			
			vo.setEquipe(equipe);
			
			vo.setAgendadoPor(getBlocoCirurgicoPortalPlanejamentoFacade()
					.obterNomeIntermediarioPacienteAbreviado(
							getProntuarioOnlineFacade().obterNomeProfissional(
									vo.getCrgSerMatricula(),
									vo.getCrgSerVinCodigo())));
		}
		
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	private void ordenarLista(List<RelatorioCirurgiasPendenteRetornoVO> lista) {
		final ComparatorChain comparatorChain = new ComparatorChain();
		final BeanComparator dataComparator = new BeanComparator(
				RelatorioCirurgiasPendenteRetornoVO.Fields.DATA.toString(), new NullComparator(false));
		final BeanComparator nroAgendaComparator = new BeanComparator(
				RelatorioCirurgiasPendenteRetornoVO.Fields.NUMERO_AGENDA.toString(), new NullComparator(false));
		
		comparatorChain.addComparator(dataComparator);
		comparatorChain.addComparator(nroAgendaComparator);
	
		Collections.sort(lista, comparatorChain);
	}
	
	public NomeArquivoRelatorioCrgVO gerarRelatorioCirurgiasPendenteRetornoCSV(
			DominioTipoPendenciaCirurgia tipoPendenciaCirurgia, Short unfSeq, 
			Date dtInicio, Date dtFim, Integer pciSeq, String extensaoArquivo) 
					throws ApplicationBusinessException, IOException {
		
		final List<RelatorioCirurgiasPendenteRetornoVO> lista = pesquisarCirurgiasPendenteRetorno(
				tipoPendenciaCirurgia, unfSeq, dtInicio, dtFim, pciSeq);
		
		Locale locale = new Locale(LOCALE_LANG, LOCALE_COUNTRY);
		DateFormat dateFormatDiaMesAno = new SimpleDateFormat(DATE_PATTERN_DD_MM_YY, locale);
		
		String nomeRelatorio = DominioNomeRelatorio.MBCR_NOTAS_PENDENTES.toString();
		
		final File file = File.createTempFile(nomeRelatorio, extensaoArquivo);

		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODING);
		
		out.write("Data" + SEPARADOR + "Agenda" + SEPARADOR + "Origem" + SEPARADOR 
				+ "Equipe" + SEPARADOR + "Esp" + SEPARADOR + "Agendado por" + SEPARADOR
				+ "Paciente" + SEPARADOR + "Prontuário" + SEPARADOR + "DC" + QUEBRA_LINHA);
		
		for (RelatorioCirurgiasPendenteRetornoVO vo : lista) {
			out.write(dateFormatDiaMesAno.format(vo.getData()) + SEPARADOR + vo.getNumeroAgenda() + SEPARADOR + vo.getOrigem() + SEPARADOR 
					+ vo.getEquipe() + SEPARADOR + vo.getEspSigla() + SEPARADOR + vo.getAgendadoPor() + SEPARADOR  
					+ vo.getNome() + SEPARADOR + vo.getStrProntuario() + SEPARADOR + vo.getDc() + QUEBRA_LINHA);
		}
		
		out.flush();
		out.close();
		
		NomeArquivoRelatorioCrgVO nomeArquivoRelatorioCrgVO = new NomeArquivoRelatorioCrgVO();
		nomeArquivoRelatorioCrgVO.setFileName(file.getAbsolutePath());
		nomeArquivoRelatorioCrgVO.setNomeRelatorio(nomeRelatorio + extensaoArquivo);
		
		return nomeArquivoRelatorioCrgVO;
	}
	
	public void validarTipoPendenciaRelatorioCirurgiasPendenteRetorno(DominioTipoPendenciaCirurgia tipoPendenciaCirurgia)
			throws ApplicationBusinessException {
		if (tipoPendenciaCirurgia == null) {
			throw new ApplicationBusinessException(
					RelatorioCirurgiasPendenteRetornoONExceptionCode.ERRO_RELATORIO_CIRURGIAS_PENDENTE_RETORNO_TIPO_DE_PENDENCIA_NAO_INFORMADO);
		}
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected MbcDescricaoCirurgicaDAO getMbcDescricaoCirurgicaDAO() {
		return mbcDescricaoCirurgicaDAO;
	}
	
	protected PdtDescricaoDAO getPdtDescricaoDAO() {
		return pdtDescricaoDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected IBlocoCirurgicoPortalPlanejamentoFacade getBlocoCirurgicoPortalPlanejamentoFacade() {
		 return this.iBlocoCirurgicoPortalPlanejamentoFacade;
	}	
	
	protected IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return this.iProntuarioOnlineFacade;
	}

}
