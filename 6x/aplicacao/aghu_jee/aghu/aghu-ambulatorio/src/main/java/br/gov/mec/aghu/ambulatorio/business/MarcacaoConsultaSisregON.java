package br.gov.mec.aghu.ambulatorio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasSisregDAO;
import br.gov.mec.aghu.model.AacConsultasSisreg;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe contendo os métodos negociais necessários para a marcação de consultas
 * importadas do SISREG
 * 
 * @author diego.pacheco  
 * 
 */
@Stateless
public class MarcacaoConsultaSisregON extends BaseBusiness {

	private static final long serialVersionUID = -5073735046143399813L;

	private static final String NEWLINE = System.getProperty("line.separator");
	private static final Log LOG = LogFactory.getLog(MarcacaoConsultaSisregON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private MarcacaoConsultaSisRegBean marcacaoConsultaSisRegBean;
	
	@Inject
	private AacConsultasSisregDAO aacConsultasSisregDAO;
	
	
	public enum MarcacaoConsultaSisregONExceptionCode implements
			BusinessExceptionCode {
		ERRO_MARCACAO_CONSULTA_SISREG_INSERIR_PACIENTE, 
		ERRO_MARCACAO_CONSULTA_SISREG_PESQUISAR_PACIENTE_CNS_DUPLICIDADE, ERRO_MARCACAO_CONSULTA_SISREG_PESQUISAR_PACIENTE_DADOS_DUPLICIDADE,
		ERRO_MARCACAO_CONSULTA_SISREG_CONSULTA_CORRESPONDENTE,
		ERRO_MARCACAO_CONSULTA_SISREG_SERVIDOR_NAO_ENCONTRADO,ERRO_MARCACAO_CONSULTA_SISREG_CONVENIO_DEFAULT;

		public void throwException(Object... params)
				throws ApplicationBusinessException{
			throw new ApplicationBusinessException(this, params);
		}

	}
	
	public List<AacConsultasSisreg> obterConsultasSisreg() {
		return getAacConsultasSisregDAO().pesquisarConsultasSisregNaoImportadas();
	}
	
	public StringBuilder marcarConsultas(List<AacConsultasSisreg> consultasSisreg, Integer totalConsultas, String nomeMicrocomputador) {
		Integer consultasMarcadas = 0;
		Integer consultasComErro = 0;

		StringBuilder log = new StringBuilder(226);
		log.append("Marcar Consultas Agendadas");

		for (AacConsultasSisreg consultaSisreg : consultasSisreg) {
			Integer linha = consultaSisreg.getLinhaArquivo();
			LOG.info("LINHA:::::::: " + linha);
			LOG.info("TIPO::::::::: " + consultaSisreg.getTipoConsulta());
			try {				
				marcacaoConsultaSisRegBean.processarRegistroSisreg(consultaSisreg, nomeMicrocomputador);
				
			} catch (ApplicationBusinessException e) {
				log.append(NEWLINE)
				.append("Erro ao processar consulta da linha ").append(linha).append(", do paciente com o CNS ").append(consultaSisreg.getCnsPaciente())
				.append(". Motivo: " ).append(getResourceBundleValue(e));
				++consultasComErro;
				continue;
			} catch (Exception t) {
				log.append(NEWLINE)
				.append("Erro ao processar consulta da linha ").append(linha).append(", do paciente com o CNS ").append(consultaSisreg.getCnsPaciente())
				.append(". Motivo: " ).append(  t.getLocalizedMessage());
				++consultasComErro;
				continue;
			}
			++consultasMarcadas;
		}
		log.append(NEWLINE);
		if (consultasComErro == 0) {
			log.append("Marcação de consultas realizada com sucesso");
			log.append(NEWLINE);	
		}
		log.append("Consultas marcadas: ").append(consultasMarcadas)
		.append(NEWLINE)
		.append("Consultas com erro: ").append(consultasComErro)
		.append(NEWLINE)
		.append("Total de Consultas: ").append(totalConsultas);
		LOG.info(log.toString());

		return log;
	}
	
	protected AacConsultasSisregDAO getAacConsultasSisregDAO() {
		return aacConsultasSisregDAO;
	}
	
		
}
