package br.gov.mec.aghu.comissoes.prescricao.comissaoprontuarios.business;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.AipLogProntOnlines;
import br.gov.mec.aghu.model.MpmMotivoReinternacao;

/**
 * Porta de entrada do submódulo Comissão Prontuários, do submódulo prescrição
 * do módulo de Comissão.
 * 
 * @author lcmoura
 * 
 */


@Stateless
public class ComissaoProntuariosPrescricaoComissoesFacade extends BaseFacade implements IComissaoProntuariosPrescricaoComissoesFacade{

@EJB
private MotivoReinternacaoON motivoReinternacaoON;
@EJB
private ConsultaArquivoLogPOLON consultaArquivoLogPOLON;
@EJB
private GeraArquivoLogPOLON geraArquivoLogPOLON;


	/**
	 * 
	 */
	private static final long serialVersionUID = 5286204325486659697L;

	@Override
	public String gerarCSVLogProntuarioOnline(List<AipLogProntOnlines> logs) throws IOException{
		return getGeraArquivoLogPOLON().gerarCSVLogProntuarioOnline(logs);
	}
	
	
	@Override
	public String nameHeaderEfetuarDownloadCSVLogProntuarioOnline(final String fileName) {
		return getGeraArquivoLogPOLON().nameHeaderEfetuarDownloadCSVLogProntuarioOnline();
	}

	@Override
	public void validarIntervaloData(Date inicio, Date fim) throws ApplicationBusinessException {
		getConsultaArquivoLogPOLON().validarIntervaloData(inicio, fim);
	}

	@Override
	public MpmMotivoReinternacao inserirAtualizarMotivoReinternacao(
			MpmMotivoReinternacao elemento)
			throws ApplicationBusinessException {
		return getMotivoReinternacaoON().inserirAtualizar(elemento);
	}

	@Override
	public MpmMotivoReinternacao obterMotivoReinternacaoPorChavePrimaria(Integer chavePrimaria) {
		return getMotivoReinternacaoON().obterPorChavePrimaria(chavePrimaria);
	}

	@Override
	public List<MpmMotivoReinternacao> pesquisarMotivoReinternacao(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MpmMotivoReinternacao elemento) {
		return getMotivoReinternacaoON().pesquisar(firstResult, maxResult, orderProperty, asc, elemento);
	}

	@Override
	public Long pesquisarMotivoReinternacaoCount(MpmMotivoReinternacao elemento) {
		return getMotivoReinternacaoON().pesquisarCount(elemento);
	}

	@Override
	public void removerMotivoReinternacao(final Integer seq) throws ApplicationBusinessException {
		getMotivoReinternacaoON().remover(seq);
	}

	protected MotivoReinternacaoON getMotivoReinternacaoON() {
		return motivoReinternacaoON;
	}

	protected GeraArquivoLogPOLON getGeraArquivoLogPOLON(){
		return geraArquivoLogPOLON;
	}
	
	protected ConsultaArquivoLogPOLON getConsultaArquivoLogPOLON() {
		return consultaArquivoLogPOLON;
	}
	
}
