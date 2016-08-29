package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmFichaApache;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.Dominio;

@Stateless
public class VisualizacaoFichaApacheON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(VisualizacaoFichaApacheON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	private static final long serialVersionUID = 3270733972781248568L;
 
	/**
	 * Codigo pll MPMF_ATU_FICHA_APACH soma_escore_apache
	 * 
	 * @param fichaApche
	 * @return
	 */
	public Integer calcularPontuacaoFichaApache(MpmFichaApache fichaApche) {
		
		if (fichaApche.getEscalaGlasgow() == null) {
			return null;
		}
		
		Integer pontuacao =
				Math.abs(nvl(fichaApche.getPontuacaoApacheTemperatura(), 0)) +
				Math.abs(nvl(fichaApche.getPontuacaoApachePressaoArterialMedia(), 0)) +
				Math.abs(nvl(fichaApche.getPontuacaoApacheFrequenciaCardiaca(), 0)) +
				Math.abs(nvl(fichaApche.getPontuacaoApacheFrequenciaRespiratoria(), 0)) +
				Math.abs(nvl(fichaApche.getPontuacaoApachePHArterial(), 0)) +
				Math.abs(nvl(fichaApche.getPontuacaoApacheSodioPlasmatico(), 0)) +
				Math.abs(nvl(fichaApche.getPontuacaoApachePotassioPlasmatico(), 0)) +
				Math.abs(nvl(fichaApche.getPontuacaoApacheHematocrito(), 0)) +
				Math.abs(nvl(fichaApche.getPontuacaoApacheLeucocitometria(), 0)) +
				Math.abs(nvl(fichaApche.getPontuacaoApacheIdade(), 0)) +
				Math.abs(nvl(fichaApche.getPontuacaoApacheO2Fio2Maior05(), 0)) +
				Math.abs(nvl(fichaApche.getPontuacaoApacheO2Fio2Menor05(), 0)) +
				(15 - Math.abs(nvl(fichaApche.getEscalaGlasgow().getPontAberturaOcular(), 0) +
						nvl(fichaApche.getEscalaGlasgow().getPontComunicacaoVerbal(), 0) +
						nvl(fichaApche.getEscalaGlasgow().getPontRespostaMotora(), 0)
						));

		if  (Boolean.TRUE.equals(fichaApche.getCreatinaSericaAguda())) {
			pontuacao = Math.abs(nvl(fichaApche.getPontuacaoApacheCreatininaSerica(), 0) * 2) + pontuacao;
		}
		else {
			pontuacao = Math.abs(nvl(fichaApche.getPontuacaoApacheCreatininaSerica(), 0)) + pontuacao;
		}
		
		if (Boolean.TRUE.equals(fichaApche.getDoencaFigado())
			|| Boolean.TRUE.equals(fichaApche.getDoencaCoracao())
			|| Boolean.TRUE.equals(fichaApche.getDoencaPulmao())
			|| Boolean.TRUE.equals(fichaApche.getDoencaRenal())
			|| Boolean.TRUE.equals(fichaApche.getDoencaImunologica())
			|| Boolean.TRUE.equals(fichaApche.getPosOperatorioUrgencia())) {
			pontuacao += 5;			
		}
		
		if (Boolean.TRUE.equals(fichaApche.getCirurgiaProgramada())) {
			pontuacao += 2;			
		}
			
		return pontuacao;
	}
	
	protected Integer nvl(Dominio obj1, Integer obj2) {
		if (obj1 == null) {
			return obj2;
		}
		return obj1.getCodigo();
	}

	
}