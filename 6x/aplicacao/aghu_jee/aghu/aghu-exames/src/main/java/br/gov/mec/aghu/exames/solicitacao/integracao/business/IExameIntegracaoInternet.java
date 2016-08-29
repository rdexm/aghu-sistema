package br.gov.mec.aghu.exames.solicitacao.integracao.business;

import java.io.ByteArrayOutputStream;
import java.io.File;

import br.gov.mec.aghu.exames.solicitacao.vo.DadosEnvioExameInternetVO;
import br.gov.mec.aghu.exames.solicitacao.vo.DadosRetornoExameInternetVO;


public interface IExameIntegracaoInternet {

	public String gerarXmlEnvio(DadosEnvioExameInternetVO dadosEnvioExameInternetVO);
	
	public File gerarArquivoCompactado(byte[] arquivoLaudo, String xmlEnvio);		
	
	public DadosRetornoExameInternetVO enviarLaudoPortal(Integer soeSeq, Integer grupoSeq, String localizador, File arquivoLaudo, String urlBase, String senha);
	
	public ByteArrayOutputStream gerarArquivoCompactadoEmMemoria(byte[] arquivoLaudo, String xmlEnvio);
	
}