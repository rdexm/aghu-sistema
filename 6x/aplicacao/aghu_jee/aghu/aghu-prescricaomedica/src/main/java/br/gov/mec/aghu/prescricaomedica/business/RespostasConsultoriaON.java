package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioFinalizacao;
import br.gov.mec.aghu.prescricaomedica.dao.MpmRespostaConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.RespostasConsultoriaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RespostasConsultoriaON extends BaseBusiness {

	private static final long serialVersionUID = 1581641434259476740L;

	@Inject
	private MpmRespostaConsultoriaDAO respostaConsultoriaDAO;

	private static final Log LOG = LogFactory.getLog(RespostasConsultoriaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private static final String NEWLINE = System.getProperty("line.separator");

	/**
	 * @ORADB MPMP_MONTA_RESPOSTA
	 * @param atdSeq
	 * @param scnSeq
	 * @param ordem
	 */
	public String pesquisarRespostasConsultoriaPorAtdSeqConsultoria(Integer atdSeq, Integer scnSeq, Integer ordem) {
		List<RespostasConsultoriaVO> respostas = new ArrayList<RespostasConsultoriaVO>();
		Date criadoEmAnt = null;
		StringBuilder builder, tituloDados;

		respostas = this.respostaConsultoriaDAO.listarRespostaConsultoriaPorAtdSeqConsultoria(atdSeq, scnSeq, ordem);
		
		builder = new StringBuilder();
		for (RespostasConsultoriaVO resposta : respostas) {
			tituloDados = new StringBuilder();
			tituloDados.append("* Resposta em ");
			tituloDados.append(DateUtil.obterDataFormatada(resposta.getCriadoEm(), "dd/MM/yyyy HH:mm"));
			tituloDados.append(" consultoria ");
			
			if (resposta.getIndFinalizacao() != null) {
				if (resposta.getIndFinalizacao().equals(DominioFinalizacao.A)) {
					tituloDados.append("EM ACOMPANHAMENTO ");
				} else if (resposta.getIndFinalizacao().equals(DominioFinalizacao.S)) {
					tituloDados.append("AVALIADA E CONCLUIDA ");
				} 
			}
			
			tituloDados.append("por ");
			tituloDados.append(resposta.getNome()).append(NEWLINE).append(NEWLINE);
			
			if (criadoEmAnt != null && (DateUtil.obterDataFormatada(resposta.getCriadoEm(), "dd/MM/yyyy HH:mm").equals(DateUtil.obterDataFormatada(criadoEmAnt, "dd/MM/yyyy HH:mm")))) {
				builder.append(resposta.getTipo()).append(NEWLINE);
				builder.append(resposta.getDescricao()).append(NEWLINE);
			} else {
				builder.append(tituloDados.toString());
				builder.append(resposta.getTipo()).append(NEWLINE);
				builder.append(resposta.getDescricao()).append(NEWLINE);
				
				criadoEmAnt = resposta.getCriadoEm();
			}
			
			builder.append(NEWLINE);
		}
		
		return builder.toString();
	}
}