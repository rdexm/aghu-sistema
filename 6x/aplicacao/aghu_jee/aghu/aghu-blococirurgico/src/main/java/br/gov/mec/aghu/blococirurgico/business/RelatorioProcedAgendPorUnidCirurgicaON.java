package br.gov.mec.aghu.blococirurgico.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.NomeArquivoRelatorioCrgVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProcedAgendPorUnidCirurgicaVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioProcedAgendPorUnidCirurgicaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioProcedAgendPorUnidCirurgicaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;


	@EJB
	private IParametroFacade iParametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1509458164835661724L;
	
	private static final String DATE_PATTERN_DD_MM_YYYY = "dd/MM/yyyy";
	private static final String DATE_PATTERN_HH_MM = "HH:mm";
	private static final String LOCALE_LANG = "pt";
	private static final String LOCALE_COUNTRY = "BR";
	private static final String ENCODING = "ISO-8859-1";
	private static final String SEPARADOR = ";";
	private static final String QUEBRA_LINHA = "\n";
	private static final String CARACTER_RETORNO_LINHA = "\r";
	
	public enum RelatorioProcedAgendPorUnidCirurgicaONExceptionCode implements	BusinessExceptionCode {
		ERRO_RELATORIO_PROCED_AGEND_POR_UNID_CIRURGICA_LIMITE_INTERVALO,  ERRO_RELATORIO_DATA_INICIO_MAIOR_DO_QUE_DATA_FIM;
	}
	
	public List<RelatorioProcedAgendPorUnidCirurgicaVO> pesquisarCirurgiaAgendadaProcedPrincipalAtivoPorUnfSeqPciSeqDtInicioDtFim(
			Short unfSeq, Integer pciSeq, Date dtInicio, Date dtFim) {
		
		final List<RelatorioProcedAgendPorUnidCirurgicaVO> lista = 
				getMbcCirurgiasDAO().pesquisarCirurgiaAgendadaProcedPrincipalAtivoPorUnfSeqPciSeqDtInicioDtFim(unfSeq, pciSeq, dtInicio, dtFim);
		
		MbcProfCirurgiasDAO profCirurgiasDAO = getMbcProfCirurgiasDAO();
		DominioFuncaoProfissional[] arrayFuncoesMedico = {DominioFuncaoProfissional.MCO, DominioFuncaoProfissional.MPF};
		
		for (RelatorioProcedAgendPorUnidCirurgicaVO vo : lista) {
			StringBuilder cirurgioesSb = new StringBuilder(500);
			
			List<MbcProfCirurgias> listaProf = profCirurgiasDAO.pesquisarProfissionalPorCrgSeqEFuncoes(
					vo.getCrgSeq(), arrayFuncoesMedico);
			
			for (MbcProfCirurgias profCirurgia : listaProf) {
				String nomeUsual = profCirurgia.getServidorPuc().getPessoaFisica().getNomeUsual();
				String nomeExibido = "";
				if (nomeUsual == null) {
					nomeExibido = profCirurgia.getServidorPuc().getPessoaFisica().getNome().substring(0, 15);
				} else {
					nomeExibido = nomeUsual;
				}
				cirurgioesSb.append(nomeExibido);
				cirurgioesSb.append(QUEBRA_LINHA);
			}
			vo.setCirurgioes(cirurgioesSb.toString());
			//procedimentos
			List<MbcProcEspPorCirurgias> listaProcEsp = new ArrayList<MbcProcEspPorCirurgias>();
			MbcProcEspPorCirurgiasDAO mbcProcEspDAO = this.getMbcProcEspCirurgiasDAO();
			listaProcEsp = mbcProcEspDAO.pesquisarProcEspPorCrgSeq(vo.getCrgSeq());
			StringBuilder descricaoProcedimentos = new StringBuilder(500);
			for (MbcProcEspPorCirurgias mbcProcEsp : listaProcEsp) {
				descricaoProcedimentos.append(mbcProcEsp.getMbcEspecialidadeProcCirgs().getMbcProcedimentoCirurgicos().getDescricao()); 
				descricaoProcedimentos.append(QUEBRA_LINHA);
			}
			vo.setDescricaoProcedimento(descricaoProcedimentos.toString());
		}
		
		return lista;
	}
	
	public NomeArquivoRelatorioCrgVO gerarRelatorioProcedAgendPorUnidCirurgicaCSV(
			Short unfSeq, Integer pciSeq, Date dtInicio, Date dtFim, String extensaoArquivo) throws IOException {
		
		final List<RelatorioProcedAgendPorUnidCirurgicaVO> lista = 
				pesquisarCirurgiaAgendadaProcedPrincipalAtivoPorUnfSeqPciSeqDtInicioDtFim(unfSeq, pciSeq, dtInicio, dtFim);
		
		Locale locale = new Locale(LOCALE_LANG, LOCALE_COUNTRY);
		DateFormat dateFormatDiaMesAno = new SimpleDateFormat(DATE_PATTERN_DD_MM_YYYY, locale);
		DateFormat dateFormatHoraMinuto = new SimpleDateFormat(DATE_PATTERN_HH_MM, locale);
		
		String nomeRelatorio = DominioNomeRelatorio.MBCR_CIR_AGENDADA.toString();
		
		final File file = File.createTempFile(nomeRelatorio, extensaoArquivo);

		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODING);
		
		out.write("Data" + SEPARADOR + "Sala" + SEPARADOR + "Início" + SEPARADOR 
				+ "Fim" + SEPARADOR + "Procedimentos" + SEPARADOR + "Cirurgião" + QUEBRA_LINHA);
		
		for (RelatorioProcedAgendPorUnidCirurgicaVO vo : lista) {
			Date dthrInicio = vo.getDthrInicio();
			String dthrInicioStr = "";
			if (dthrInicio != null) {
				dthrInicioStr = dateFormatHoraMinuto.format(dthrInicio);
			}
			
			Date dthrFim = vo.getDthrFim();
			String dthrFimStr = "";
			if (dthrFim != null) {
				dthrFimStr = dateFormatHoraMinuto.format(dthrFim);
			}
			
			String cirurgioes = ajustarQuebraLinhaMesmaColuna(vo.getCirurgioes());
			String procedimentos =  ajustarQuebraLinhaMesmaColuna(vo.getDescricaoProcedimento());
			
			out.write(dateFormatDiaMesAno.format(vo.getData()) + SEPARADOR + vo.getSciSeqp() + SEPARADOR + dthrInicioStr + SEPARADOR 
					+ dthrFimStr + SEPARADOR + procedimentos + SEPARADOR + cirurgioes + QUEBRA_LINHA);
		}
		
		out.flush();
		out.close();
		
		NomeArquivoRelatorioCrgVO nomeArquivoRelatorioCrgVO = new NomeArquivoRelatorioCrgVO();
		nomeArquivoRelatorioCrgVO.setFileName(file.getAbsolutePath());
		nomeArquivoRelatorioCrgVO.setNomeRelatorio(nomeRelatorio + extensaoArquivo);
		
		return nomeArquivoRelatorioCrgVO;
	}	
	
	public void validarIntervaloEntreDatasRelatorioProcedAgendPorUnidCirurgica(
			final Date dataInicio, final Date dataFim) throws ApplicationBusinessException {
		
		AghParametros parametroIntervaloEntreDatas = getParametroFacade()
				.obterAghParametro(AghuParametrosEnum.P_AGHU_LIM_DT_PROCED_AGEND_POR_UNID_CIRG);

		if (parametroIntervaloEntreDatas != null
				&& !(DateUtil.calcularDiasEntreDatasComPrecisao(dataInicio,
						dataFim, 10).compareTo(
						parametroIntervaloEntreDatas.getVlrNumerico()) < 0)) {
			throw new ApplicationBusinessException(
					RelatorioProcedAgendPorUnidCirurgicaONExceptionCode.ERRO_RELATORIO_PROCED_AGEND_POR_UNID_CIRURGICA_LIMITE_INTERVALO);
		}
	}
	
	
	private String ajustarQuebraLinhaMesmaColuna(String str) {
		str = StringUtils.removeEnd(str, QUEBRA_LINHA); 
		str = StringUtils.replace(str, QUEBRA_LINHA, CARACTER_RETORNO_LINHA);
		str = "\"" + str + "\"";
		return str;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}
	
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

}
