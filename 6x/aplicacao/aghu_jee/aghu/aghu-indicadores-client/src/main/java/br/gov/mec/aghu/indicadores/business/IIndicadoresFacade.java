package br.gov.mec.aghu.indicadores.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.dominio.DominioTipoIndicador;
import br.gov.mec.aghu.dominio.DominioTipoUnidade;
import br.gov.mec.aghu.indicadores.vo.IndHospClinicaEspVO;
import br.gov.mec.aghu.indicadores.vo.UnidadeIndicadoresVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinIndicadorHospitalarResumido;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Local
public interface IIndicadoresFacade extends Serializable {
	
	void gerarIndicadoresHospitalares(Date date, String cron) throws ApplicationBusinessException;

	void gravarIndicadoresResumidos(Date date, String cron) throws ApplicationBusinessException;
	
	

	public void gravarIndicadoresResumidos(Date anoMesCompetencia) throws ApplicationBusinessException;

	public void gerarIndicadoresHospitalares(Date anoMesCompetencia) throws ApplicationBusinessException;

	public List<AinIndicadorHospitalarResumido> obterTotaisIndicadoresUnidade(Date mes, Date mesFim,
			DominioTipoIndicador tipoIndicador, AghUnidadesFuncionais unidadeFuncional);

	public Integer obterNumeroOcorrenciasIndicadoresGerais(Date mes);

	public List<IndHospClinicaEspVO> gerarRelatorioClinicaEspecialidade(Date mes);

	public Integer obterNumeroOcorrenciasIndicadoresUnidade(DominioTipoUnidade tipoUnidade, Date mes);

	public List<UnidadeIndicadoresVO> pesquisarIndicadoresUnidade(DominioTipoUnidade tipoUnidade, Date mes);


	public Date obterUltimaDataInicial();
	
}
