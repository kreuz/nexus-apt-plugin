package com.inventage.nexusaptplugin.cache.generators;

import java.io.ByteArrayOutputStream;

import javax.inject.Named;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.openpgp.PGPPublicKey;

import com.inventage.nexusaptplugin.cache.FileGenerator;
import com.inventage.nexusaptplugin.cache.RepositoryData;
import com.inventage.nexusaptplugin.sign.AptSigningConfiguration;
import com.inventage.nexusaptplugin.sign.PGPSigner;

import com.google.inject.Inject;


@Named
public class SignKeyGenerator
        implements FileGenerator {
    private final AptSigningConfiguration configuration;

    @Inject
    public SignKeyGenerator(AptSigningConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public byte[] generateFile(RepositoryData data)
            throws Exception {
        // Extract the key and return it
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PGPSigner signer = configuration.getSigner();
        PGPPublicKey publicKey = signer.getSecretKey().getPublicKey();

        BCPGOutputStream out = new BCPGOutputStream(new ArmoredOutputStream(baos));
        publicKey.encode(out);

        out.close();
        baos.close();

        return baos.toByteArray();
    }

}
