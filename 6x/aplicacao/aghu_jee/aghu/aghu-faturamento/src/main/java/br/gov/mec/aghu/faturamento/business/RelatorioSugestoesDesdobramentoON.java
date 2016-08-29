package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioOrigemSugestoesDesdobramento;
import br.gov.mec.aghu.faturamento.vo.SugestoesDesdobramentoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioSugestoesDesdobramentoON extends BaseBusiness {

	private static final long serialVersionUID = -3463329543889117027L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioSugestoesDesdobramentoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String EXTENSAO=".csv";
	
	public String gerarCSV(DominioOrigemSugestoesDesdobramento origem, String incialPac) throws IOException, BaseException{
		
		List<SugestoesDesdobramentoVO> listVO = faturamentoFacade.pesquisarSugestoesDesdobramento(origem, incialPac);
		
		final File file = File.createTempFile(DominioNomeRelatorio.RELATORIO_SUGESTOES_DESDOBRAMENTO.getDescricao(), EXTENSAO);
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		out.write("Leito" + SEPARADOR + "Orig" + SEPARADOR + "DtHr Sugestão" + SEPARADOR + "Motivo Desdobramento" + SEPARADOR + "Paciente" + SEPARADOR
				+ "Conta" + SEPARADOR + "Int Adm" + SEPARADOR + "Alta Adm\n");
		
		for (final SugestoesDesdobramentoVO sdVO : listVO) {
			
			sdVO.setDescricao(sdVO.getDescricao().toUpperCase());
			sdVO.setNome(sdVO.getNome().toUpperCase());
			
			out.write(checarNulo(sdVO.getLtoId()) + SEPARADOR 
					+ checarNulo(sdVO.getOrigem()) + SEPARADOR 
					+ checarNulo(sdVO.getDthrSugestao()) + SEPARADOR
					+ checarNulo(sdVO.getDescricao()) + SEPARADOR
					+ checarNulo(sdVO.getProntuario() + " " + checarNulo(sdVO.getNome())) + SEPARADOR
					+ checarNulo(sdVO.getCthSeq()) + SEPARADOR
					+ checarNulo(sdVO.getDtInternacaoAdministrativa()) + SEPARADOR
					+ checarNulo(sdVO.getDtAltaAdministrativa()) + "\n");
		}
		
		out.write("CF_NOME_EMPRESA\n" + "Hospital de Clínicas de Porto Alegre\n");
		
		out.flush();
		out.close();

		return file.getAbsolutePath();
		
	}
	
	private Object checarNulo(Object objeto) {
		String retorno = StringUtils.EMPTY;

		if (objeto != null) {
			if (objeto instanceof Date) {
				retorno = DateUtil.obterDataFormatada((Date) objeto, "dd/MM/yyyy");
			} else {
				retorno = String.valueOf(objeto);
			}
		}
		return retorno;
	}
	
}
