package br.gov.mec.aghu.internacao.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.dao.AinMovimentoInternacaoDAO;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.RelatorioAltasDiaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Classe responsável por prover os métodos de negócio genéricos para o
 * relatório de Altas Dia.
 * 
 */
@Stateless
public class RelatorioAltasDiaON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioAltasDiaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO;

@EJB
private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6066171000865032864L;

	@SuppressWarnings("PMD.NPathComplexity")
	public List<RelatorioAltasDiaVO> pesquisa(Date dataAlta) {
		
		List<RelatorioAltasDiaVO> lista = new ArrayList<RelatorioAltasDiaVO>();
		
		List<Object[]> result = getAinMovimentoInternacaoDAO().pesquisaAltasDia(dataAlta);
		if (result != null && !result.isEmpty()) {
			
			Iterator<Object[]> it = result.iterator();

			while (it.hasNext()) {
				Object[] obj = it.next();
				RelatorioAltasDiaVO vo = new RelatorioAltasDiaVO();

				if (obj[0] != null) {
					vo.setCodigoConvenio((String) obj[0].toString());
				}

				if (obj[1] != null && obj[2] != null) {
					StringBuilder sb = new StringBuilder();
					Formatter formatter = new Formatter(sb, Locale.US);

					SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");

					vo.setDescricaoConvenioData(formatter.format("%-50s", obj[1]).toString().concat("Data: ")
							.concat(formatador.format(obj[2])));
				}

				if (obj[3] != null) {
					String prontAux = ((Integer) obj[3]).toString();
					vo.setProntuario(prontAux.substring(0, prontAux.length() - 1) + "/" + prontAux.charAt(prontAux.length() - 1));
				}

				if (obj[4] != null) {
					vo.setNomePaciente((String) obj[4]);
				}

				if (obj[5] != null) {
					String tipoObito = (String) obj[5];
					if (tipoObito.equals("C") || tipoObito.equals("D")) {
						vo.setObito("O");
					} else {
						vo.setObito("");
					}
				}

				if (obj[6] != null && obj[7] != null) {
					vo.setCrm(getPesquisaInternacaoFacade().buscarNroRegistroConselho((Short) obj[6], (Integer) obj[7]));
					vo.setNomeMedico(getPesquisaInternacaoFacade().buscarNomeUsual((Short) obj[6], (Integer) obj[7]));
				}

				if (obj[8] != null) {
					Date ultData = getPesquisaInternacaoFacade().buscaUltimaAlta((Integer) obj[8]);
					SimpleDateFormat formatador = new SimpleDateFormat("yyyy");
					SimpleDateFormat formatadorRetorno = new SimpleDateFormat("dd/MM/yy");

					if (ultData != null) {
						vo.setDataAnt((formatador.format(ultData).compareTo("2001") == 0 ? null : formatadorRetorno.format(ultData)));
					}
					vo.setSenha(getPesquisaInternacaoFacade().buscaSenhaInternacao((Integer) obj[8]));
				}

				if (obj[9] != null) {
					SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yy");

					vo.setDataInt(formatador.format((Date) obj[9]));
				}

				if (obj[10] != null) {
					vo.setSiglaEspecialidade((String) obj[10]);
				}

				if (obj[11] != null) { // Leito
					vo.setLeito((String) obj[11]);
				} else {
					if (obj[12] != null) { // Numero do quarto
						vo.setLeito((String) obj[12].toString());
					} else { // Andar
						if (obj[13] != null) {
							String strT = (String) obj[13].toString();

							vo.setLeito((String) ("0".concat(strT)).substring(strT.length() == 1 ? 0 : 1));
						}
					}
				}

				lista.add(vo);
			}
		}
		return lista;
	
	}

	protected AinMovimentoInternacaoDAO getAinMovimentoInternacaoDAO(){
		return ainMovimentoInternacaoDAO;
	}
	
	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade(){
		return pesquisaInternacaoFacade;
	}
}
