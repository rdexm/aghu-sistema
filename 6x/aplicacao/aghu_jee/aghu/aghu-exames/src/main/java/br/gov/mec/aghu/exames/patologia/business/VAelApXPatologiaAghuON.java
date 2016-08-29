package br.gov.mec.aghu.exames.patologia.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
import br.gov.mec.aghu.dominio.DominioConvenioExameSituacao;
import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSimNaoNaoAplicavel;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.dao.VAelApXPatologiaAghuDAO;
import br.gov.mec.aghu.exames.patologia.vo.ConsultaItensPatologiaVO;
import br.gov.mec.aghu.exames.vo.MedicoSolicitanteVO;
import br.gov.mec.aghu.model.AelApXPatologista;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelNomenclaturaEspecs;
import br.gov.mec.aghu.model.AelNomenclaturaGenerics;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AelTopografiaAparelhos;
import br.gov.mec.aghu.model.AelTopografiaSistemas;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.VAelApXPatologiaAghu;
import br.gov.mec.aghu.paciente.vo.ConvenioExamesLaudosVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class VAelApXPatologiaAghuON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(VAelApXPatologiaAghuON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private VAelApXPatologiaAghuDAO vAelApXPatologiaAghuDAO;
	
	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
 
	private static final long serialVersionUID = 7909495452363015724L;

	@SuppressWarnings({"unchecked"})
	public List<VAelApXPatologiaAghu> pesquisarVAelApXPatologiaAghu(final Integer firstResult, final Integer maxResults, String orderProperty,
			boolean asc, final AelPatologista residenteResp, final DominioSituacaoExamePatologia situacaoExmAnd, final Date dtDe, final Date dtAte,
			final AelPatologista patologistaResp, final AelConfigExLaudoUnico exame, final Long numeroAp,
			final MedicoSolicitanteVO medicoSolic, final AelExameAp material, final DominioConvenioExameSituacao convenio, final DominioSimNao laudoImpresso,
			final AelNomenclaturaGenerics nomenclaturaGenerica,
			final AelNomenclaturaEspecs nomenclaturaEspecifica,
			final AelTopografiaSistemas topografiaSistema,
			final AelTopografiaAparelhos topografiaAparelho,
			final DominioSimNao neoplasiaMaligna, final DominioSimNaoNaoAplicavel margemComprometida, final DominioSimNao biopsia,
			boolean apenasNroAp
	) throws BaseException {
		
		List<VAelApXPatologiaAghu> list = getVAelApXPatologiaAghuDAO().pesquisarVAelApXPatologiaAghu(firstResult, maxResults, orderProperty, asc, 
				residenteResp, situacaoExmAnd, dtDe, dtAte, 
				patologistaResp, exame, numeroAp, medicoSolic, 
				material, convenio, laudoImpresso, nomenclaturaGenerica, 
				nomenclaturaEspecifica, topografiaSistema, 
				topografiaAparelho, neoplasiaMaligna, 
				margemComprometida, biopsia, apenasNroAp);
		

		if(list != null && !list.isEmpty()){
			
			// Filtrar por convenio
			if(convenio != null){
				List<VAelApXPatologiaAghu> result = new ArrayList<VAelApXPatologiaAghu>();
				for (VAelApXPatologiaAghu vAghu : list) {
					if(vAghu.getId().getAtdSeq() != null || vAghu.getId().getAtvSeq() != null){
						
						if(DominioConvenioExameSituacao.S.equals(convenio)){
							final DominioSimNao isSus = getExamesPatologiaFacade().aelcBuscaConvGrp(vAghu.getId().getAtdSeq(), vAghu.getId().getAtvSeq());
							
							if(DominioSimNao.S.equals(isSus)){
								ConvenioExamesLaudosVO conv = getExamesPatologiaFacade().aelcBuscaConvLaud(vAghu.getId().getAtdSeq(), vAghu.getId().getAtvSeq());
								vAghu.setConvenio(conv.getDescricaoConvenio());
								
								result.add(vAghu);
							} 
						} else {
							final DominioSimNao isSus = getExamesPatologiaFacade().aelcBuscaConvGrp(vAghu.getId().getAtdSeq(), vAghu.getId().getAtvSeq());
							
							if(DominioSimNao.N.equals(isSus)){
								ConvenioExamesLaudosVO conv = getExamesPatologiaFacade().aelcBuscaConvLaud(vAghu.getId().getAtdSeq(), vAghu.getId().getAtvSeq());
								vAghu.setConvenio(conv.getDescricaoConvenio());
								
								result.add(vAghu);
							} 
						}
					}
				}
				
				list.clear();
				list = result;
				
			// Apenas popula a coluna convenio
			} else {
				for (VAelApXPatologiaAghu vAghu : list) {
					if(vAghu.getId().getAtdSeq() != null || vAghu.getId().getAtvSeq() != null){
						ConvenioExamesLaudosVO conv = getExamesPatologiaFacade().aelcBuscaConvLaud(vAghu.getId().getAtdSeq(), vAghu.getId().getAtvSeq());
						vAghu.setConvenio(conv.getDescricaoConvenio());
					}
				}
			}
			
			//popula nro vias para imprimir
			List<VAelApXPatologiaAghu> removeList = new ArrayList<VAelApXPatologiaAghu>();

			populaNroVias(list, removeList);

			removeItens(list, removeList);

			
			if (VAelApXPatologiaAghu.Fields.CONVENIO.toString().equalsIgnoreCase(orderProperty)){
					final ComparatorChain ordenacao = new ComparatorChain();
					final BeanComparator ordenarConvenio = new BeanComparator(VAelApXPatologiaAghu.Fields.CONVENIO.toString(), 
							new NullComparator(false));
					
					ordenacao.addComparator(ordenarConvenio);
					
					Collections.sort(list, ordenacao);

				if (list.size() > maxResults.intValue()) {
					list = obterListaPaginada(list, firstResult, maxResults);
				}

			} else if (convenio != null || orderProperty == null || VAelApXPatologiaAghu.Fields.LUM_NUMERO_AP.toString().equalsIgnoreCase(orderProperty)) {
				if (orderProperty == null) {
					Collections.sort(list, new VAelApXPatologiaAghuComparator());	
				} else if (VAelApXPatologiaAghu.Fields.LUM_NUMERO_AP.toString().equalsIgnoreCase(orderProperty)) {
					if (asc) {
						Collections.sort(list, new VAelApXPatologiaAghuComparator());	
					} else {
						Collections.sort(list, Collections.reverseOrder(new VAelApXPatologiaAghuComparator()));
					}
				}
				
				if (list.size() > maxResults.intValue()) {
					list = obterListaPaginada(list, firstResult, maxResults);
				}
			} 
			
			return this.recuperaNomePatologista(list);
			
		} else {
			return this.recuperaNomePatologista(list);
		}
	}
	
	private void populaNroVias(List<VAelApXPatologiaAghu> list, List<VAelApXPatologiaAghu> removeList) throws ApplicationBusinessException {
		AghParametros pCalc = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_CAL_SEQ_LAUDO_UNICO);
		AghParametros pSitLiberada = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		AghParametros pSitAreaExec = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		AghParametros pSitExec = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_EXECUTANDO);
		
		for (VAelApXPatologiaAghu vAghu : list) {
			Integer nroVias = 1;
			final DominioSimNao isSus = getExamesPatologiaFacade().aelcBuscaConvGrp(vAghu.getId().getAtdSeq(), vAghu.getId().getAtvSeq());
			if(DominioSimNao.S.equals(isSus)){
				try {
					nroVias = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_VIAS_LAUDO_UNICO_SUS).getVlrNumerico().intValue();
				} catch (ApplicationBusinessException e) {
					logError(e);
				}
			}
			else if(DominioSimNao.N.equals(isSus)){
				try {
					nroVias = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_VIAS_LAUDO_UNICO_NSUS).getVlrNumerico().intValue();
				} catch (ApplicationBusinessException e) {
					logError(e);
				}
			}
			vAghu.setNroVias(nroVias);
			
			
			// ITENS
			List<ConsultaItensPatologiaVO> itensExame = getExamesPatologiaFacade().listaExamesComVersaoLaudo(vAghu.getId().getLuxSeq(), pCalc.getVlrNumerico().intValue(), new String[]{pSitLiberada.getVlrTexto(), pSitAreaExec.getVlrTexto(), pSitExec.getVlrTexto()});

			if (itensExame.isEmpty()) {
				removeList.add(vAghu);
			}		
		}
		
	}

	private void removeItens(List<VAelApXPatologiaAghu> list, List<VAelApXPatologiaAghu> removeList) {
		
		for (VAelApXPatologiaAghu vAghu : removeList) {
			list.remove(vAghu);
		}
		
	}
	
	private List<VAelApXPatologiaAghu> recuperaNomePatologista(List<VAelApXPatologiaAghu> list) {
		final IExamesPatologiaFacade examesPatologiaFacade = this.getExamesPatologiaFacade();
		for (final VAelApXPatologiaAghu vAelApXPatologiaAghu : list) {
			final AelApXPatologista patologista = examesPatologiaFacade.obterAelApXPatologistaPorSeqAnatoPatologicoMatriculaEFuncao(vAelApXPatologiaAghu
					.getId().getLumSeq(), vAelApXPatologiaAghu.getId().getMatriculaPatologista(), new DominioFuncaoPatologista[] { DominioFuncaoPatologista.P,
					DominioFuncaoPatologista.C }, DominioSituacao.A);
			if (patologista == null) {
				LOG.warn("Não encontrou o servidor matrícula " + vAelApXPatologiaAghu.getId().getMatriculaPatologista());
			} else {
				vAelApXPatologiaAghu.getId().setNomePatologista(patologista.getAelPatologista().getServidor().getPessoaFisica().getNome());
			}
			final AelApXPatologista residente = examesPatologiaFacade.obterAelApXPatologistaPorSeqAnatoPatologicoMatriculaEFuncao(vAelApXPatologiaAghu.getId()
					.getLumSeq(), vAelApXPatologiaAghu.getId().getMatriculaResidente(), new DominioFuncaoPatologista[] { DominioFuncaoPatologista.R },
					DominioSituacao.A);
			if (residente == null) {
				LOG.warn("Não encontrou o servidor matrícula " + vAelApXPatologiaAghu.getId().getMatriculaResidente());
			} else {
				vAelApXPatologiaAghu.getId().setNomeResidente(residente.getAelPatologista().getServidor().getPessoaFisica().getNome());
			}
		}
		return list;
	}
	
	private List<VAelApXPatologiaAghu> obterListaPaginada(List<VAelApXPatologiaAghu> list, Integer firstResult, Integer maxResults) {
		return list.subList(firstResult, (firstResult+maxResults) > list.size() ? list.size() : (firstResult+maxResults));
	}

	public Integer pesquisarVAelApXPatologiaAghuCount(final AelPatologista residenteResp, final DominioSituacaoExamePatologia situacaoExmAnd, final Date dtDe, final Date dtAte,
			final AelPatologista patologistaResp, final AelConfigExLaudoUnico exame, final Long numeroAp,
			final MedicoSolicitanteVO medicoSolic, final AelExameAp material, final DominioConvenioExameSituacao convenio, 
			final DominioSimNao laudoImpresso,
			final AelNomenclaturaGenerics nomenclaturaGenerica,
			final AelNomenclaturaEspecs nomenclaturaEspecifica,
			final AelTopografiaSistemas topografiaSistema,
			final AelTopografiaAparelhos topografiaAparelho,
			final DominioSimNao neoplasiaMaligna, final DominioSimNaoNaoAplicavel margemComprometida, final DominioSimNao biopsia) {
		
		// Filtrar por convenio
		if(convenio != null){
			@SuppressWarnings("unchecked")
			List<VAelApXPatologiaAghu> list = (List<VAelApXPatologiaAghu>) getVAelApXPatologiaAghuDAO().pesquisarVAelApXPatologiaAghuCount(
					residenteResp, situacaoExmAnd, dtDe, dtAte, 
					patologistaResp, exame, numeroAp, medicoSolic, 
					material, convenio, laudoImpresso, 
					nomenclaturaGenerica, nomenclaturaEspecifica, 
					topografiaSistema, topografiaAparelho, neoplasiaMaligna, margemComprometida, 
					biopsia);
			
			int result=0;
			
			for (VAelApXPatologiaAghu vAghu : list) {
				if(vAghu.getId().getAtdSeq() != null || vAghu.getId().getAtvSeq() != null){
					
					if(DominioConvenioExameSituacao.S.equals(convenio)){
						final DominioSimNao isSus = getExamesPatologiaFacade().aelcBuscaConvGrp(vAghu.getId().getAtdSeq(), vAghu.getId().getAtvSeq());
						
						if(DominioSimNao.S.equals(isSus)){
							result++;
						} 
					} else {
						final DominioSimNao isSus = getExamesPatologiaFacade().aelcBuscaConvGrp(vAghu.getId().getAtdSeq(), vAghu.getId().getAtvSeq());
						
						if(DominioSimNao.N.equals(isSus)){
							result++;
						} 
					}
				}
			}
			
			return result;
			
		// Apenas popula a coluna convenio
		} else {
			return (Integer) getVAelApXPatologiaAghuDAO().pesquisarVAelApXPatologiaAghuCount(residenteResp, situacaoExmAnd, dtDe, dtAte, 
					patologistaResp, exame, numeroAp, medicoSolic, 
					material, convenio, laudoImpresso, nomenclaturaGenerica, 
					nomenclaturaEspecifica, topografiaSistema, topografiaAparelho, 
					neoplasiaMaligna, margemComprometida, biopsia);
		}
	}
	
	/**
	 * Comparator para ordenar pesquisa pelo número de AP de forma crescente.
	 * Obs.: para a ordenação é considerada a parte sequencial e a parte do ano do número AP separadamente.
	 * 
	 * @author diego.pacheco
	 *
	 */
	static class VAelApXPatologiaAghuComparator implements Comparator<VAelApXPatologiaAghu> {
		@Override
		public int compare(VAelApXPatologiaAghu v1, VAelApXPatologiaAghu v2) {
			String strNumeroApV1 = v1.getId().getLumNumeroAp().toString();
			String strNumeroApV2 = v2.getId().getLumNumeroAp().toString();
			
			int tamStrNumeroApV1 = strNumeroApV1.length();
			int tamStrNumeroApV2 = strNumeroApV2.length();
			
			String strAnoNumeroApV1 = "";
			String strAnoNumeroApV2 = "";
			String strSeqNumeroApV1 = "";
			String strSeqNumeroApV2 = "";
			
			if (tamStrNumeroApV1 >= 3 && tamStrNumeroApV2 >= 3) {
				strAnoNumeroApV1 = strNumeroApV1.substring(tamStrNumeroApV1 - 2);
				strAnoNumeroApV2 = strNumeroApV2.substring(tamStrNumeroApV2 - 2);
				strSeqNumeroApV1 = strNumeroApV1.substring(0, tamStrNumeroApV1 - 2);
				strSeqNumeroApV2 = strNumeroApV2.substring(0, tamStrNumeroApV2 - 2);
				
				int compAno = Long.valueOf(strAnoNumeroApV1).compareTo(Long.valueOf(strAnoNumeroApV2));
				int compSeq = Long.valueOf(strSeqNumeroApV1).compareTo(Long.valueOf(strSeqNumeroApV2));

				if (compAno != 0) {
					return compAno;
				}
				
				return compSeq;
			}
			
			return 0;
		}
	}	
	
	protected VAelApXPatologiaAghuDAO getVAelApXPatologiaAghuDAO() {
		return vAelApXPatologiaAghuDAO;
	}

	protected IExamesPatologiaFacade getExamesPatologiaFacade() {
		return this.examesPatologiaFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}

}