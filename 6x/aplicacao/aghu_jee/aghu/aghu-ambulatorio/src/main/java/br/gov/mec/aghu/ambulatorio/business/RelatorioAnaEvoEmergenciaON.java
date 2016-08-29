package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioGrupoProfissionalAnamnese;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.vo.RelatorioAnaEvoInternacaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioAnaEvoEmergenciaON extends BaseBusiness {


@EJB
private RelatorioAnaEvoInternacaoRN relatorioAnaEvoInternacaoRN;

private static final Log LOG = LogFactory.getLog(RelatorioAnaEvoEmergenciaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

@EJB
private ICascaFacade cascaFacade;

@Inject
private MamEvolucoesDAO mamEvolucoesDAO;

@Inject
private MamAnamnesesDAO mamAnamnesesDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private RelatorioAnaEvoInternacaoBeanLocal relatorioAnaEvoInternacaoBeanLocal;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3217430401326632770L;

	public enum RelatorioAnaEvoEmergenciaONExceptionCode implements BusinessExceptionCode{
		DATA_FIM_DEVE_SER_MAIOR_IGUAL_DATA_INICIO;
	}
	
	public List<RelatorioAnaEvoInternacaoVO> pesquisarRelatorioAnaEvoEmergencia(
			Long trgSeq, Date dtHrValidaInicio, Date dtHrValidaFim,
			DominioGrupoProfissionalAnamnese grupoProssional, Integer conNumero, CseCategoriaProfissional categoriaProfissional)
			throws ApplicationBusinessException {
		IParametroFacade parametroFacade = getParametroFacade();
		RelatorioAnaEvoInternacaoBeanLocal relatorioAnaEvoInternacaoBean = getRelatorioAnaEvoInternacaoBean();
		
		AghParametros parametroMed = parametroFacade.obterAghParametro(AghuParametrosEnum.P_GRUPO_PROFISSIONAL_ANAMNESE_MED);
		AghParametros parametroEnf = parametroFacade.obterAghParametro(AghuParametrosEnum.P_GRUPO_PROFISSIONAL_ANAMNESE_ENF);
		AghParametros parametroNut = parametroFacade.obterAghParametro(AghuParametrosEnum.P_GRUPO_PROFISSIONAL_ANAMNESE_NUT);
		
		if(dtHrValidaFim != null && dtHrValidaInicio.after(dtHrValidaFim)){
			throw new ApplicationBusinessException(RelatorioAnaEvoEmergenciaONExceptionCode.DATA_FIM_DEVE_SER_MAIOR_IGUAL_DATA_INICIO);
		}
		
		List<RelatorioAnaEvoInternacaoVO> tmp = new ArrayList<RelatorioAnaEvoInternacaoVO>();
		List<RelatorioAnaEvoInternacaoVO> result = new ArrayList<RelatorioAnaEvoInternacaoVO>();
		
		dtHrValidaInicio = DateUtil.obterDataComHoraInical(dtHrValidaInicio);
		dtHrValidaFim = DateUtil.obterDataComHoraFinal(dtHrValidaFim);
		
		List<RelatorioAnaEvoInternacaoVO> listAnaEvo = relatorioAnaEvoInternacaoBean.complementarDadosComTipoItemEvolucao(getMamEvolucoesDAO().pesquisarEvolucaoAtendimentoEmergencia(trgSeq, dtHrValidaInicio, dtHrValidaFim));
		List<RelatorioAnaEvoInternacaoVO> listCompl = relatorioAnaEvoInternacaoBean.complementarDadosComTipoItemAnamnese(getMamAnamnesesDAO().pesquisarAnamnesesPorTriagemDataHoraValida(trgSeq, dtHrValidaInicio, dtHrValidaFim));
		
		tmp.addAll(listAnaEvo);
		tmp.addAll(listCompl);
		
		for (RelatorioAnaEvoInternacaoVO vo : tmp) {
			if (validarGrupoProfissionais(vo, grupoProssional)) {				
				if (vo.getAnamnese()) {
					relatorioAnaEvoInternacaoBean.complementarDadosRelatorioAnamneses(vo, parametroMed, parametroEnf, parametroNut);				
				}
				if (vo.getEvolucao()) {
					relatorioAnaEvoInternacaoBean.complementarDadosRelatorioEvolucao(vo, categoriaProfissional);
				}
				result.add(vo);
			}
		}
		
		if (!result.isEmpty()) {
			RelatorioAnaEvoInternacaoVO vo = result.get(0);
			vo.setTrgSeq(trgSeq);
			vo.setConNumero(conNumero);
			relatorioAnaEvoInternacaoBean.complementarRodapeRelatorioAnaEvoInternacao(vo, Boolean.TRUE);
			result.add(complementarTriagemRelatorioAnaEvo(trgSeq));
		}
		
		return result;
	}
	
	private Boolean validarGrupoProfissionais(RelatorioAnaEvoInternacaoVO vo, DominioGrupoProfissionalAnamnese grupo) throws ApplicationBusinessException {
		Boolean valida = (grupo == null);
		
		RapServidores servidorValida = null;
		
		if (vo.getAnamnese()) {
			servidorValida = getMamAnamnesesDAO().obterPorChavePrimaria(vo.getAnaSeq()).getServidorValida();
		} else if (vo.getEvolucao()) {
			servidorValida = getMamEvolucoesDAO().obterPorChavePrimaria(vo.getEvoSeq()).getServidorValida();
		}
		
		if(servidorValida != null){

			List<String> listPermite = new ArrayList<String>();
			List<String> listNaoPermite = new ArrayList<String>();				
			Set<String> nomePerfisServidor = Collections.emptySet();
			if(servidorValida.getUsuario() != null) {
				nomePerfisServidor = getICascaFacade().obterNomePerfisPorUsuario(servidorValida.getUsuario());
			}
			AghParametros param = null;
			if (!nomePerfisServidor.isEmpty() && !DominioGrupoProfissionalAnamnese.P_GRUPO_PROFISSIONAL_ANAMNESE_OPS.equals(grupo)) {
				if (DominioGrupoProfissionalAnamnese.P_GRUPO_PROFISSIONAL_ANAMNESE_ENF.equals(grupo)) {
					param = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_GRUPO_PROFISSIONAL_ANAMNESE_ENF);
					listPermite.addAll(Arrays.asList(param.getVlrTexto().split(",")));
				} else if (DominioGrupoProfissionalAnamnese.P_GRUPO_PROFISSIONAL_ANAMNESE_MED.equals(grupo)) {
					param = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_GRUPO_PROFISSIONAL_ANAMNESE_MED);
					listPermite.addAll(Arrays.asList(param.getVlrTexto().split(",")));
				} else if (DominioGrupoProfissionalAnamnese.P_GRUPO_PROFISSIONAL_ANAMNESE_NUT.equals(grupo)) {
					param = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_GRUPO_PROFISSIONAL_ANAMNESE_NUT);	
					listPermite.addAll(Arrays.asList(param.getVlrTexto().split(",")));
				} else if (grupo != null) {
					param = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_GRUPO_PROFISSIONAL_ANAMNESE_ENF);
					listNaoPermite.addAll(Arrays.asList(param.getVlrTexto().split(",")));
					param = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_GRUPO_PROFISSIONAL_ANAMNESE_MED);
					listNaoPermite.addAll(Arrays.asList(param.getVlrTexto().split(",")));
					param = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_GRUPO_PROFISSIONAL_ANAMNESE_NUT);	
					listNaoPermite.addAll(Arrays.asList(param.getVlrTexto().split(",")));
				}
				
				for (String nomePerfil : nomePerfisServidor) {
					if (!listPermite.isEmpty()) {
						valida = listPermite.contains(nomePerfil);
					} else if (!listNaoPermite.isEmpty()) {
						valida = !listNaoPermite.contains(nomePerfil);
					}
				}
			} else if (nomePerfisServidor.isEmpty() && DominioGrupoProfissionalAnamnese.P_GRUPO_PROFISSIONAL_ANAMNESE_OPS.equals(grupo)) {
				valida = Boolean.TRUE;
			}
		}							
		return valida;
	}
	
	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	private RelatorioAnaEvoInternacaoVO complementarTriagemRelatorioAnaEvo(Long trgSeq) throws ApplicationBusinessException {
		RelatorioAnaEvoInternacaoVO vo  = new RelatorioAnaEvoInternacaoVO();
		vo.setTriagem(Boolean.TRUE);
		vo.setTrgConteudo(getRelatorioAnaEvoInternacaoRN().obterEmergenciaVisTriagem(trgSeq, null).toString());
		return vo;
	}
	
	protected MamAnamnesesDAO getMamAnamnesesDAO() {
		return mamAnamnesesDAO;
	}
	
	protected MamEvolucoesDAO getMamEvolucoesDAO() {
		return mamEvolucoesDAO;
	}
	
	protected RelatorioAnaEvoInternacaoBeanLocal getRelatorioAnaEvoInternacaoBean() {
		return this.relatorioAnaEvoInternacaoBeanLocal;
	}
	
	protected RelatorioAnaEvoInternacaoRN getRelatorioAnaEvoInternacaoRN() {
		return relatorioAnaEvoInternacaoRN;
	}
}
