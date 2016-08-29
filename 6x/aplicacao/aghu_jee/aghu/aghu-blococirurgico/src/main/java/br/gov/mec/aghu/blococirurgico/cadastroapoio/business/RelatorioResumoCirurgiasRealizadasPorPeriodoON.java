package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioResumoCirurgiasRealizadasPorPeriodoListVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioResumoCirurgiasRealizadasPorPeriodoVO;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RelatorioResumoCirurgiasRealizadasPorPeriodoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioResumoCirurgiasRealizadasPorPeriodoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 2396491815090457486L;
	
	public enum RelatorioResumoCirurgiasRealizadasPorPeriodoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

	public RelatorioResumoCirurgiasRealizadasPorPeriodoVO buscaDadosRelatorio(AghUnidadesFuncionais unidadeCirurgica, Date dataInicial, Date dataFinal) throws ApplicationBusinessException {
		
		List<MbcCirurgias> listaCirurgias = getMbcCirurgiasDAO().buscarCirurgias(unidadeCirurgica, dataInicial, dataFinal);
		
		if(listaCirurgias.isEmpty()){
			throw new ApplicationBusinessException(RelatorioResumoCirurgiasRealizadasPorPeriodoONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}
		
		int totalCanceladas = 0;
		int totalPendentesRet = 0;
		int totalEmerg = 0;
		int totalUrg = 0;
		int totalEletiva = 0;
		int totalCirurgRealizadas = 0;
		
		for (MbcCirurgias cirurgia : listaCirurgias) {
		
			/* sum(decode(CRG.SITUACAO,'CANC',1,0)) TOTAL_CANCELADAS */
			if(cirurgia.getSituacao().equals(DominioSituacaoCirurgia.CANC)){
				totalCanceladas = totalCanceladas + 1;
			}
			
		    /* sum(decode(CRG.SITUACAO,'CANC',0,DECODE(CRG.IND_DIGT_NOTA_SALA,'N',1,0))) TOTAL_PENDENTES_RET */
			if(!cirurgia.getSituacao().equals(DominioSituacaoCirurgia.CANC) & cirurgia.getDigitaNotaSala().equals(Boolean.FALSE)){
				totalPendentesRet = totalPendentesRet + 1;
			}
			
			/* sum(decode(CRG.SITUACAO,'CANC',0,DECODE(CRG.NATUREZA_AGEND,'EMG', DECODE(CRG.IND_DIGT_NOTA_SALA,'S',1,0),0))) TOTAL_EMERG */
			if(!cirurgia.getSituacao().equals(DominioSituacaoCirurgia.CANC) & cirurgia.getNaturezaAgenda().equals(DominioNaturezaFichaAnestesia.EMG) & cirurgia.getDigitaNotaSala().equals(Boolean.TRUE)){
				totalEmerg = totalEmerg + 1;
			}
			
			/* sum(decode(CRG.SITUACAO,'CANC',0,DECODE(CRG.NATUREZA_AGEND,'URG', DECODE(CRG.IND_DIGT_NOTA_SALA,'S',1,0),0))) TOTAL_URG */
			if(!cirurgia.getSituacao().equals(DominioSituacaoCirurgia.CANC) & cirurgia.getNaturezaAgenda().equals(DominioNaturezaFichaAnestesia.URG) & cirurgia.getDigitaNotaSala().equals(Boolean.TRUE)){
				totalUrg = totalUrg + 1;
			}
			
			/* sum(decode(CRG.SITUACAO,'CANC',0,DECODE(CRG.NATUREZA_AGEND,'ELE', DECODE(CRG.IND_DIGT_NOTA_SALA,'S',1,0), DECODE(CRG.NATUREZA_AGEND,'ESP', DECODE(CRG.IND_DIGT_NOTA_SALA,'S',1,0),0)))) TOTAL_ELETIVA */
			if(!cirurgia.getSituacao().equals(DominioSituacaoCirurgia.CANC) & cirurgia.getNaturezaAgenda().equals(DominioNaturezaFichaAnestesia.ELE) & cirurgia.getDigitaNotaSala().equals(Boolean.TRUE)){
				totalEletiva = totalEletiva + 1;
			} else if(cirurgia.getNaturezaAgenda().equals(DominioNaturezaFichaAnestesia.ESP) & cirurgia.getDigitaNotaSala().equals(Boolean.TRUE)){
				totalEletiva = totalEletiva + 1;
			}
			
			/* sum(decode(CRG.SITUACAO,'CANC',0,DECODE(CRG.IND_DIGT_NOTA_SALA,'S',1,0))) TOTAL_CIRURG_REALIZADAS */
			if(!cirurgia.getSituacao().equals(DominioSituacaoCirurgia.CANC) & cirurgia.getDigitaNotaSala().equals(Boolean.TRUE)){
				totalCirurgRealizadas = totalCirurgRealizadas + 1;
			}
			
		}
		
		RelatorioResumoCirurgiasRealizadasPorPeriodoVO dados = new RelatorioResumoCirurgiasRealizadasPorPeriodoVO();
		dados.setTotalCanceladas(totalCanceladas);
		dados.setTotalPendentesRet(totalPendentesRet);
		dados.setTotalCirurgRealizadas(totalCirurgRealizadas);
		dados.setTotalEletiva(totalEletiva);
		dados.setTotalEmerg(totalEmerg);
		dados.setTotalUrg(totalUrg);
		
		return dados;
	}

	public List<RelatorioResumoCirurgiasRealizadasPorPeriodoListVO> buscaDadosRelatorioDetalhe(AghUnidadesFuncionais unidadeCirurgica, Date dataInicial, Date dataFinal) throws ApplicationBusinessException {
		
		List<MbcCirurgias> listaCirurgiasDetalhe = getMbcCirurgiasDAO().buscarCirurgiasDetalhe(unidadeCirurgica, dataInicial, dataFinal);
		
		if(listaCirurgiasDetalhe.isEmpty()){
			throw new ApplicationBusinessException(RelatorioResumoCirurgiasRealizadasPorPeriodoONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}
		
		List<AghEspecialidades> listaEspecialidades = new ArrayList<AghEspecialidades>();
		
		for (MbcCirurgias cirurgia : listaCirurgiasDetalhe) {
			if(!listaEspecialidades.contains(cirurgia.getEspecialidade())){
				listaEspecialidades.add(cirurgia.getEspecialidade());
			}
		}
		
		List<RelatorioResumoCirurgiasRealizadasPorPeriodoListVO> listaDados = new ArrayList<RelatorioResumoCirurgiasRealizadasPorPeriodoListVO>();
		for (AghEspecialidades especialidade : listaEspecialidades) {
		
			RelatorioResumoCirurgiasRealizadasPorPeriodoListVO dadosDetalhe = new RelatorioResumoCirurgiasRealizadasPorPeriodoListVO();
			
			dadosDetalhe.setSigla(especialidade.getSigla());
			dadosDetalhe.setNomeEspecialidade(especialidade.getNomeEspecialidade());
			
			int emergencia = 0;
			int urgencia = 0;
			int eletiva = 0;
			int canceladas = 0;
			int realizadas = 0;
			
			for (MbcCirurgias cirurgiaDetalhe : listaCirurgiasDetalhe) {
				
				if(dadosDetalhe.getSigla().equalsIgnoreCase(cirurgiaDetalhe.getEspecialidade().getSigla()) & dadosDetalhe.getNomeEspecialidade().equalsIgnoreCase(cirurgiaDetalhe.getEspecialidade().getNomeEspecialidade())){
					
					/* sum(decode(CRG1.SITUACAO,'CANC',0,DECODE(CRG1.NATUREZA_AGEND,'EMG',DECODE(CRG1.IND_DIGT_NOTA_SALA,'S',1,0),0))) EMERGENCIA */
					if(!cirurgiaDetalhe.getSituacao().equals(DominioSituacaoCirurgia.CANC) & cirurgiaDetalhe.getNaturezaAgenda().equals(DominioNaturezaFichaAnestesia.EMG) & cirurgiaDetalhe.getDigitaNotaSala().equals(Boolean.TRUE)){
						emergencia = emergencia + 1;
					}
					
					/* sum(decode(CRG1.SITUACAO,'CANC',0, DECODE(CRG1.NATUREZA_AGEND,'URG',DECODE(CRG1.IND_DIGT_NOTA_SALA,'S',1,0),0))) URGENCIA */
					if(!cirurgiaDetalhe.getSituacao().equals(DominioSituacaoCirurgia.CANC) & cirurgiaDetalhe.getNaturezaAgenda().equals(DominioNaturezaFichaAnestesia.URG) & cirurgiaDetalhe.getDigitaNotaSala().equals(Boolean.TRUE)){
						urgencia = urgencia + 1;
					}
					
					/* sum(decode(CRG1.SITUACAO,'CANC',0,DECODE(CRG1.NATUREZA_AGEND,'ELE',DECODE(CRG1.IND_DIGT_NOTA_SALA,'S',1,0),DECODE(CRG1.NATUREZA_AGEND,'ESP',DECODE(CRG1.IND_DIGT_NOTA_SALA,'S',1,0),0)))) ELETIVA */
					if(!cirurgiaDetalhe.getSituacao().equals(DominioSituacaoCirurgia.CANC) & cirurgiaDetalhe.getNaturezaAgenda().equals(DominioNaturezaFichaAnestesia.ELE) & cirurgiaDetalhe.getDigitaNotaSala().equals(Boolean.TRUE)){
						eletiva = eletiva + 1;
					} else if(cirurgiaDetalhe.getNaturezaAgenda().equals(DominioNaturezaFichaAnestesia.ESP) & cirurgiaDetalhe.getDigitaNotaSala().equals(Boolean.TRUE)){
						eletiva = eletiva + 1;
					}
					
					/* sum(decode(CRG1.SITUACAO,'CANC',DECODE(mtc1.ind_Erro_AGEND,'N',1,0),0)) CANCELADAS */
					if(cirurgiaDetalhe.getSituacao().equals(DominioSituacaoCirurgia.CANC)){
						canceladas = canceladas + 1;
					}
										
					/* sum(decode(CRG1.SITUACAO,'CANC',0,DECODE(CRG1.IND_DIGT_NOTA_SALA,'S',1,0))) REALIZADAS */
					if(!cirurgiaDetalhe.getSituacao().equals(DominioSituacaoCirurgia.CANC) & cirurgiaDetalhe.getDigitaNotaSala().equals(Boolean.TRUE)){
						realizadas = realizadas + 1;
					}
				}
			}
			
			dadosDetalhe.setCanceladas(canceladas);
			dadosDetalhe.setEletiva(eletiva);
			dadosDetalhe.setEmergencia(emergencia);
			dadosDetalhe.setRealizadas(realizadas);
			dadosDetalhe.setUrgencia(urgencia);
	
			listaDados.add(dadosDetalhe);
		}

		return listaDados;
	}

}
