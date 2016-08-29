package br.gov.mec.aghu.administracao.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AacUnidFuncionalSalas;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;


@Stateless
public class MicrocomputadorON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MicrocomputadorON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	private static final long serialVersionUID = 5950500354025867167L;
	
	/**
	* Obtém o nome abreviado da Unidade Funcional
	*/
	public String obterDescricaoUnidadeFuncionalAbreviada(AghMicrocomputador microcomputador) throws BaseException {
		String nomeUnidFunc = "";
		if(microcomputador.getAghUnidadesFuncionais()!=null) {
			nomeUnidFunc = getAghuFacade().obterDescricaoVAghUnidFuncional(microcomputador.getAghUnidadesFuncionais().getSeq());
		}
		if(nomeUnidFunc==null) {
			nomeUnidFunc = microcomputador.getAghUnidadesFuncionais().getDescricao();
		}
		return obterAbreviado(nomeUnidFunc);
	}
	
	/**
	* Função auxiliar para evitar que ocorra quebra de linha na tabela de resultados.
	*/
	public String obterAbreviado(String texto) {
		if(texto.length()>35) {
			String textoAbreviado = StringUtils.abbreviate(texto, 28);
			textoAbreviado = textoAbreviado.concat("...");
			return textoAbreviado;
		}
		else {
			return texto;
		}
	}
	
	/**
	* Obtém a lista das salas existentes(distintas)
	*/
	public List<AacUnidFuncionalSalas> listarSalasPorNumeroSala(Object param) {
		List<AacUnidFuncionalSalas> salasDistinas = new ArrayList<AacUnidFuncionalSalas>();
		
		List<AacUnidFuncionalSalas> salas = getAmbulatorioFacade().obterListaSalasPeloNumeroSala(param);
		if(!salas.isEmpty()) {
			List<Byte> listaSalas = new ArrayList<Byte>();  
			for(AacUnidFuncionalSalas sala : salas) {
				if(!listaSalas.contains(sala.getId().getSala())) {
					listaSalas.add(sala.getId().getSala());
					salasDistinas.add(sala);
				}
				sala.getId().getSala();
			}
		}
		
		return salasDistinas;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	

	
}