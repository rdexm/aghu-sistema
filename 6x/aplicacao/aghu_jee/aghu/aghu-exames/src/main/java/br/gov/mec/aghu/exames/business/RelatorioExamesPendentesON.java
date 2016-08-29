package br.gov.mec.aghu.exames.business;

import java.text.Normalizer;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.dao.AelApXPatologistaDAO;
import br.gov.mec.aghu.exames.dao.AelExameApDAO;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPendentesVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioExamesPendentesON extends BaseBusiness {

@EJB
private IParametroFacade parametroFacade;

@Inject
private AelExameApDAO aelExameApDAO;

@Inject
private AelApXPatologistaDAO aelApXPatologistaDAO;
	
private static final Log LOG = LogFactory.getLog(RelatorioExamesPendentesON.class);

	@Override
	@Deprecated
	public Log getLogger() {
	return LOG;
	}
	
	private enum RelatorioExamesPendentesONExceptionCode implements BusinessExceptionCode {
		DATA_INICIAL_MAIOR_QUE_DATA_FINAL;
	}
	
	private static final long serialVersionUID = -4819028681533768840L;
	
	public final static String FORMATO_DATA_DDMMYY = "dd/MM/yy";
	public final static String FORMATO_DATA_DDMMYYYY_HHMM = "dd/MM/yyyy HH:mm";
	
	public List<RelatorioExamesPendentesVO> obterListaExamesPendentes(Date dataInicial, Date dataFinal,
			String situacao, Integer[] patologistaSeq, DominioSituacaoExamePatologia situacaoExmAnd) throws ApplicationBusinessException {
		
		if (!DateUtil.validaDataMaiorIgual(dataFinal, dataInicial)){
			throw new ApplicationBusinessException(RelatorioExamesPendentesONExceptionCode.DATA_INICIAL_MAIOR_QUE_DATA_FINAL);
		}
		
		AghParametros paramUnidadePatologica = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNID_PATOL_CIRG);
		Short unidadePatologicaSeq = paramUnidadePatologica.getVlrNumerico().shortValue();
		
		String[] sitArray;
		
		if ("EA".equals(situacao)) { //EM ANDAMENTO, agrupado sao os exames AE (AREA EXECUTORA) e EX (EXECUTANDO)
			sitArray = new String[2];
			sitArray[0] = "AE";
			sitArray[1] = "AX";
		}
		else {
			sitArray = new String[1];
			sitArray[0] = situacao;
		}		
		
		List<RelatorioExamesPendentesVO> listVo = getAelExameApDAO().obterListaExamesPendentes(dataInicial, dataFinal, sitArray, patologistaSeq,
				unidadePatologicaSeq, situacaoExmAnd);
		
		for (RelatorioExamesPendentesVO vo : listVo) {
			vo.setResidenteResponsavel(getAelApXPatologistaDAO().obterResidenteResponsavel(vo.getNumeroAp(), vo.getLu2Seq()));
			vo.setDataHoraEventoRel(DateUtil.obterDataFormatada(vo.getDataHoraEvento(), FORMATO_DATA_DDMMYYYY_HHMM));
			vo.setProntuarioRel(CoreUtil.formataProntuario(vo.getProntuario()));
			vo.setDiasPendente(CoreUtil.diferencaEntreDatasEmDias(new Date(), vo.getDataHoraEvento()).intValue());
		}
		CoreUtil.ordenarLista(listVo, RelatorioExamesPendentesVO.Fields.DIAS_PENDENTE.toString(), false);
		// Chama a ordenação personalizada pois deve desconsiderar acentos nos nomes.
		ordenarLista(listVo);
		
		return listVo;
	}
	
	private void ordenarLista(List<RelatorioExamesPendentesVO> listVo) {
		Collections.sort(listVo, new Comparator<RelatorioExamesPendentesVO>() {
			@Override
			public int compare(RelatorioExamesPendentesVO o1,RelatorioExamesPendentesVO o2) {

				return removeAcentos(o1.getPatologistaResponsavel()).compareTo(removeAcentos(o2.getPatologistaResponsavel()));
			}
		}); 
	}
	
	public String removeAcentos(String str) {
		  str = Normalizer.normalize(str, Normalizer.Form.NFD);
		  str = str.replaceAll("[^\\p{ASCII}]", "");
		  return str;
	}
	
	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}
	
	protected AelExameApDAO getAelExameApDAO() {
		return this.aelExameApDAO;
	}
	
	protected AelApXPatologistaDAO getAelApXPatologistaDAO() {
		return aelApXPatologistaDAO;
	}
	
}