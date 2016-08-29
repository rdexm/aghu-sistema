package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.MpmEscoreSaps3;
import br.gov.mec.aghu.prescricaomedica.dao.MpmEscoreSaps3DAO;
import br.gov.mec.aghu.prescricaomedica.vo.ResultadoExamesVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class EscoreSaps3RN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9116903683713440667L;

	
	private static final Log LOG = LogFactory.getLog(EscoreSaps3RN.class);
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private MpmEscoreSaps3DAO mpmEscoreSaps3DAO;

	public void persistir(MpmEscoreSaps3 sap) {
		if(sap.getSeq() == null) {
			inserir(sap);
		} else {
			atualizar(sap);
		}
	}

	protected void inserir(MpmEscoreSaps3 sap) {
		sap.setDthrCriadoEm(new Date());
		sap.setServidor(servidorLogadoFacade.obterServidorLogado());
		mpmEscoreSaps3DAO.persistir(sap);
	}

	protected void atualizar(MpmEscoreSaps3 sap) {
		MpmEscoreSaps3 entity = mpmEscoreSaps3DAO.obterPorChavePrimaria(sap.getSeq());
		entity.setBilirrubinaTotal(sap.getBilirrubinaTotal());
		entity.setComorbidadeAids(sap.getComorbidadeAids());
		entity.setComorbidadeCancer(sap.getComorbidadeCancer());
		entity.setComorbidadeCancerHemato(sap.getComorbidadeCancerHemato());
		entity.setComorbidadeCirrose(sap.getComorbidadeCirrose());
		entity.setComorbidadeIcc(sap.getComorbidadeIcc());
		entity.setComorbidadeTratCancer(sap.getComorbidadeTratCancer());
		entity.setConcentracaoPh(sap.getConcentracaoPh());
		entity.setCreatinina(sap.getCreatinina());
		entity.setDrogasVasoativas(sap.getDrogasVasoativas());
		entity.setEscalaGlaslow(sap.getEscalaGlaslow());
		entity.setEstadoCirurgicoAdmissao(sap.getEstadoCirurgicoAdmissao());
		entity.setFreqCardiaca(sap.getFreqCardiaca());
		entity.setIdade(sap.getIdade());
		entity.setInfeccaoNosocomial(sap.getInfeccaoNosocomial());
		entity.setInfeccaoRespiratoria(sap.getInfeccaoRespiratoria());
		entity.setLeucocitos(sap.getLeucocitos());
		entity.setOxigenacao(sap.getOxigenacao());
		entity.setPermanencia(sap.getPermanencia());
		entity.setPlaquetas(sap.getPlaquetas());
		entity.setPresssaoSistolica(sap.getPresssaoSistolica());
		entity.setProcedencia(sap.getProcedencia());
		entity.setRazoesCardiovascular(sap.getRazoesCardiovascular());
		entity.setRazoesDigestivo(sap.getRazoesDigestivo());
		entity.setRazoesHepatico(sap.getRazoesHepatico());
		entity.setRazoesNeurologico(sap.getRazoesNeurologico());
		entity.setTemperaturaCorporal(sap.getTemperaturaCorporal());
		entity.setTipoAdmissao(sap.getTipoAdmissao());
		entity.setTiposCirurgia(sap.getTiposCirurgia());
		entity.setDthrAlteradoEm(new Date());
		entity.setIndSituacao(sap.getIndSituacao());
		entity.setServidorValida(servidorLogadoFacade.obterServidorLogado());
		mpmEscoreSaps3DAO.atualizar(entity);
	}
	
	public Double calcularObito(Short pontosSaps,Double somaObito, Double multiObito, Double subObito){
		Double LN = Double.valueOf(pontosSaps) + somaObito;
		Double LogNatural = Math.log(LN) * multiObito;
		Double Logit =  LogNatural + subObito;
		Double eLogit = Math.exp(Logit);
		Double LogNatural2 = 1.0000 + eLogit;
		Double obito = eLogit/LogNatural2;
		obito = (Math.floor(obito*100))/100;
		return obito;
	}
	
	public Long popularResultadosParametrosSaps3(String parametro, Integer atdSeq){
		if(StringUtils.isNotBlank(parametro)){
			List<ResultadoExamesVO> lista = new ArrayList<ResultadoExamesVO>();
			String a = parametro;
			String[] campos = StringUtils.split(a, ";");
			String campo1 = null;
			Integer campo2 = null;
			Integer campo3 = null;
			int index = 0;
			for (String campo : campos) {
				index++;
				if(index == 1){
					campo1 = campo;
				}if(index == 2){
					campo2 = Integer.valueOf(campo);
				}if(index == 3){
					campo3 = Integer.valueOf(campo);
					index = 0;
					ResultadoExamesVO item = null;
					List<ResultadoExamesVO> listaVO = examesFacade.obterResultadoExamesSaps3(atdSeq,campo1,campo2,campo3);
					if(listaVO != null){
						item = listaVO.size() > 0 ? listaVO.get(0) : null;
					}
					if(item != null){ lista.add(item); }
					campo1 = null;
					campo2 = null;
					campo3 = null;
				}
			}
			if(lista.size() == 1){
				return lista.get(0).getValor();
			}
			else if(lista.size() > 1){	
				ResultadoExamesVO selecionado = new ResultadoExamesVO();
				selecionado.setDataHora(new Date());
				for (ResultadoExamesVO item : lista) {
					if(DateUtil.validaDataMenor(item.getDataHora(),selecionado.getDataHora())){
						selecionado = item;
					}
				}
				return selecionado.getValor();
			}
			return null;
		}
		return null;
	}
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
}
