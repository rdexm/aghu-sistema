package br.gov.mec.aghu.blococirurgico.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.MbcCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.SalaCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.TipoAnestesiaVO;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.messages.MessagesUtils;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;


@Stateless
public class BlocoCirurgicoService implements IBlocoCirurgicoService {

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
    private MessagesUtils messagesUtils;
	
	public List<SalaCirurgicaVO> obterSalasCirurgicasAtivasPorUnfSeqNome(Short unfSeq, Short seqp, String nome) {
		List<SalaCirurgicaVO> result = new ArrayList<SalaCirurgicaVO>();
		List<MbcSalaCirurgica> salas = blocoCirurgicoFacade.obterSalasCirurgicasAtivasPorUnfSeqNome(unfSeq, seqp, nome);
		if (salas != null && !salas.isEmpty()) {
			for (MbcSalaCirurgica mbcSalaCirurgica : salas) {
				SalaCirurgicaVO sala = new SalaCirurgicaVO();
				sala.setUnfSeq(mbcSalaCirurgica.getId().getUnfSeq());
				sala.setSeqp(mbcSalaCirurgica.getId().getSeqp());
				sala.setNome(mbcSalaCirurgica.getNome());
				result.add(sala);
			}
		}
		return result;
	}

	@Override
	public Long obterSalasCirurgicasAtivasPorUnfSeqNomeCount(Short unfSeq, Short seqp, String nome) {
		return blocoCirurgicoFacade.obterSalasCirurgicasAtivasPorUnfSeqNomeCount(unfSeq, seqp, nome);
	}

	@Override
	public SalaCirurgicaVO obterDadosSalaCirurgica(Short unfSeq, Short seqp) {
		MbcSalaCirurgica mbcSalaCirurgica = blocoCirurgicoFacade.obterSalaCirurgicaPorId(new MbcSalaCirurgicaId(unfSeq, seqp));
		if (mbcSalaCirurgica != null) {
			SalaCirurgicaVO sala = new SalaCirurgicaVO();
			sala.setUnfSeq(mbcSalaCirurgica.getId().getUnfSeq());
			sala.setSeqp(mbcSalaCirurgica.getId().getSeqp());
			sala.setNome(mbcSalaCirurgica.getNome());
			return sala;
		}
		return null;
	}

	@Override
	public List<TipoAnestesiaVO> pequisarTiposAnestesiasAtivas(Short seq, String descricao) {
		List<TipoAnestesiaVO> result = new ArrayList<TipoAnestesiaVO>();
		List<MbcTipoAnestesias> tipos = null;

		if (seq != null) {
			tipos = blocoCirurgicoFacade.pequisarTiposAnestesiasAtivas(seq, null, 100);
		}
		if (tipos == null || tipos.isEmpty()) {
			tipos = blocoCirurgicoFacade.pequisarTiposAnestesiasAtivas(null, descricao, 100);
		}
		
		if (tipos != null && !tipos.isEmpty()) {
			for (MbcTipoAnestesias mbcTipoAnestesias : tipos) {
				result.add(new TipoAnestesiaVO(mbcTipoAnestesias.getSeq(), mbcTipoAnestesias.getDescricao()));
			}
		}

		return result;
	}

	@Override
	public Long pequisarTiposAnestesiasAtivasCount(Short seq, String descricao) {
		Long result = null;
		if (seq != null) {
			result = blocoCirurgicoFacade.pequisarTiposAnestesiasAtivasCount(seq, null);
		}
		if (result == null || result.longValue() == 0) {
			result = blocoCirurgicoFacade.pequisarTiposAnestesiasAtivasCount(null, descricao);
		}
		return result;
	}
	
	@Override
	public String obterPendenciaFichaAnestesia(Integer atdSeq){
		return this.blocoCirurgicoFacade.obterPendenciaFichaAnestesia(atdSeq);
	}
	
	/**
	 * Web Service #38487
	 * Utilizado na estória #26325
	 * @param pacCodigo
	 * @param listaUnfSeq
	 * @param dataCirurgia
	 * @return
	 */
	@Override
	public List<Object[]> obterCirurgiaDescCirurgicaPaciente(Integer pacCodigo, List<Short> listaUnfSeq, Date dataCirurgia){
		return this.blocoCirurgicoFacade.obterCirurgiaDescCirurgicaPaciente(pacCodigo, listaUnfSeq, dataCirurgia);
	}
	
	/**
	 * Web Service #37435
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	@Override
	public Object[] obterConselhoESiglaVRapServidorConselho(Integer matricula, Short vinCodigo){
		return this.blocoCirurgicoFacade.obterConselhoESiglaVRapServidorConselho(matricula, vinCodigo);
	}


	/**
	 * Web Service #38955
	 * @return
	 */
	@Override
	public List<MbcCirurgiaVO> obterCirurgiasPorPacienteEDatas(List<Date> datas, Integer pacCodigo, List<Short> listaSeqp) {
		
		List<MbcCirurgias> cirurgias = this.blocoCirurgicoFacade.obterCirurgiasPorPacienteEDatas(datas, pacCodigo, listaSeqp);
		List<MbcCirurgiaVO> listaBind = new ArrayList<MbcCirurgiaVO>();
		for(MbcCirurgias cirurgia : cirurgias){
			listaBind.add(new MbcCirurgiaVO(cirurgia.getSeq()));
		}
		return listaBind;
	}
	
	
	
	
	/**
	 * ORADB MBCP_INSERE_CIR_CO – Serviço #38902	
	 */
	@Override
	public void inserirCirurgiaDoCentroObstetrico(Integer pacCodigo
			,Short gestacaoSeqp
			,String nivelContaminacao
			,Date dataInicioProcedimento
			,Short salaCirurgicaSeqp
			,Short tempoDuracaoProcedimento
			,Short anestesiaSeqp
			,Short equipeSeqp
			,String tipoNascimento) throws ServiceException, ServiceBusinessException{
		
		
		try {
			this.blocoCirurgicoFacade.inserirCirurgiaDoCentroObstetrico(pacCodigo, gestacaoSeqp, nivelContaminacao, dataInicioProcedimento, salaCirurgicaSeqp, tempoDuracaoProcedimento, anestesiaSeqp, equipeSeqp, tipoNascimento);
		}  catch (BaseException e) {
			throw new ServiceBusinessException(messagesUtils.getResourceBundleValue(e));
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}	
		
	}	
	
	/**
	 * Web Service #39545
	 * @return
	 */
	@Override
	public Boolean verificarExisteDescricaoCirurgica(Integer crgSeq) {
		List<MbcDescricaoCirurgica> result = this.blocoCirurgicoFacade.listarDescricaoCirurgicaPorSeqCirurgia(crgSeq);
		
		if(result != null && result.size() > 0){
			return Boolean.TRUE;
		}
		
		return false;
	}
	
	/**
	 * Web Service #39546
	 * @return
	 */
	@Override
	public Boolean verificarExisteDescricaoCirurgicaPorCirurgiaEProfissional(Integer crgSeq, Integer servidorMatricula, Short servidorVinCodigo) {
		return this.blocoCirurgicoFacade.obterCountDistinctDescricaoCirurgicaPorCrgSeqEServidor(crgSeq, servidorMatricula, servidorVinCodigo) > 0 ? Boolean.TRUE : Boolean.FALSE;
	}
}
